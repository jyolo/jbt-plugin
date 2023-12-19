package tabCompletion.binary;

import tabCompletion.binary.BinaryRequest;
import tabCompletion.binary.BinaryResponse;
import tabCompletion.binary.exceptions.TabNineDeadException;
import org.jetbrains.annotations.Nullable;

public interface BinaryProcessRequester {
  @Nullable
  <R extends BinaryResponse> R request(BinaryRequest<R> request) throws TabNineDeadException;

  Long pid();

  void destroy();
}
