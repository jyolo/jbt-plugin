package tabCompletion.binary;

import tabCompletion.binary.BinaryProcessRequester;
import tabCompletion.binary.BinaryRequest;
import tabCompletion.binary.BinaryResponse;
import tabCompletion.binary.exceptions.TabNineDeadException;
import org.jetbrains.annotations.Nullable;

public class VoidBinaryProcessRequester implements tabCompletion.binary.BinaryProcessRequester {
  private static final tabCompletion.binary.BinaryProcessRequester INSTANCE = new VoidBinaryProcessRequester();

  public static BinaryProcessRequester instance() {
    return INSTANCE;
  }


  @Override
  public <R extends BinaryResponse> @Nullable R request(BinaryRequest<R> request) throws TabNineDeadException {
    return null;
  }

  @Override
  public Long pid() {
    return 0L;
  }

  @Override
  public void destroy() {}
}
