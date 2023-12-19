package tabCompletion.binary;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import tabCompletion.binary.BinaryProcessGateway;
import tabCompletion.binary.exceptions.TabNineDeadException;
import tabCompletion.binary.exceptions.TabNineInvalidResponseException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Optional;

import static java.lang.String.format;

public class ParsedBinaryIO {
  private final Gson gson;
  private final tabCompletion.binary.BinaryProcessGateway binaryProcessGateway;

  public ParsedBinaryIO(Gson gson, BinaryProcessGateway binaryProcessGateway) {
    this.gson = gson;
    this.binaryProcessGateway = binaryProcessGateway;
  }

  @NotNull
  public <R> R readResponse(Class<R> responseClass)
      throws IOException, TabNineDeadException, TabNineInvalidResponseException {
    String rawResponse = binaryProcessGateway.readRawResponse();

    try {
      return Optional.ofNullable(gson.fromJson(rawResponse, responseClass))
          .orElseThrow(
              () ->
                  new TabNineInvalidResponseException(
                      format(
                          "Binary returned null as a response for %s",
                          responseClass.getSimpleName())));
    } catch (TabNineInvalidResponseException | JsonSyntaxException e) {
      throw new TabNineInvalidResponseException(
          format(
              "Binary returned illegal response: %s for %s",
              rawResponse, responseClass.getSimpleName()),
          e,
          rawResponse);
    }
  }

  public void writeRequest(Object request) throws IOException {
    binaryProcessGateway.writeRequest(gson.toJson(request) + "\n");
  }

  public Long pid() {
    return binaryProcessGateway.pid();
  }

  public boolean isDead() {
    return binaryProcessGateway.isDead();
  }

  public void destroy() {
    binaryProcessGateway.destroy();
  }
}
