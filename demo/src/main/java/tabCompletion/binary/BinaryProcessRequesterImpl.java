package tabCompletion.binary;

import static tabCompletion.general.StaticConfig.ILLEGAL_RESPONSE_THRESHOLD;
import static tabCompletion.general.StaticConfig.wrapWithBinaryRequest;

import com.intellij.openapi.diagnostic.Logger;
import tabCompletion.binary.BinaryProcessRequester;
import tabCompletion.binary.BinaryRequest;
import tabCompletion.binary.BinaryResponse;
import tabCompletion.binary.ParsedBinaryIO;
import tabCompletion.binary.exceptions.TabNineDeadException;
import tabCompletion.binary.exceptions.TabNineInvalidResponseException;
import java.io.IOException;
import org.jetbrains.annotations.Nullable;

public class BinaryProcessRequesterImpl implements BinaryProcessRequester {
  private int illegalResponsesGiven = 0;

  private final ParsedBinaryIO parsedBinaryIO;

  public BinaryProcessRequesterImpl(ParsedBinaryIO parsedBinaryIO) {
    this.parsedBinaryIO = parsedBinaryIO;
  }

  /**
   * Request a prediction from TabNine's binary.
   *
   * @param request
   * @return an AutocompleteResponse
   * @throws TabNineDeadException if process's dead.
   * @throws TabNineDeadException if process's BufferedReader has reached its end (also mean
   *     dead...).
   * @throws TabNineDeadException if there was an IOException communicating to the process.
   * @throws TabNineDeadException if the result from the process was invalid multiple times.
   */
  @Override
  @Nullable
  public synchronized <R extends BinaryResponse> R request(BinaryRequest<R> request)
          throws TabNineDeadException {
    if (parsedBinaryIO.isDead()) {
      throw new TabNineDeadException("Binary is dead");
    }

    try {
//      if (request instanceof AutocompleteRequest) {
////        System.out.println("======request====:" + request);
//        settingsState.setInReasoning(true);
//        AutocompleteResponse autocompleteResponse = executeHttpRequest((AutocompleteRequest) request);
////        System.out.println("======autocompleteResponse====:" + autocompleteResponse);
//        settingsState.setInReasoning(false);
//        return (R) autocompleteResponse;
//      } else {
//        parsedBinaryIO.writeRequest(wrapWithBinaryRequest(request.serialize()));
//        R res = readResult(request);
//        return res;
//      }
//      System.out.println("======request.serialize()====:" + request.serialize());
//      if (request instanceof  AutocompleteRequest) {
//        System.out.println("genOldSuffix(req.before):"+ genOldPrefix(((AutocompleteRequest) request).before));
//      }
      parsedBinaryIO.writeRequest(wrapWithBinaryRequest(request.serialize()));

      R res = readResult(request);
//      System.out.println("======res====:" + res);
      return res;
    } catch (IOException e) {
      Logger.getInstance(getClass()).warn("Exception communicating with the binary", e);

      throw new TabNineDeadException(e);
    }
  }


  @Override
  public Long pid() {
    return parsedBinaryIO.pid();
  }

  @Override
  public void destroy() {
    parsedBinaryIO.destroy();
  }

  @Nullable
  private <R extends BinaryResponse> R readResult(BinaryRequest<R> request) throws IOException, TabNineDeadException {
    try {
      R response = parsedBinaryIO.readResponse(request.response());

      if (!request.validate(response)) {
        throw new TabNineInvalidResponseException();
      }

      this.illegalResponsesGiven = 0;

      return response;
    } catch (TabNineInvalidResponseException exception) {
      if (request.shouldBeAllowed(exception)) {
        return null;
      }

      if (++illegalResponsesGiven > ILLEGAL_RESPONSE_THRESHOLD) {
        illegalResponsesGiven = 0;
        throw new TabNineDeadException("Too many illegal responses given");
      } else {
        Logger.getInstance(getClass()).warn(exception);

        return null;
      }
    }
  }
}
