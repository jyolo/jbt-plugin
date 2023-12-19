package tabCompletion.binary;

import tabCompletion.binary.BinaryResponse;
import tabCompletion.binary.exceptions.TabNineInvalidResponseException;
import org.jetbrains.annotations.NotNull;

public interface BinaryRequest<R extends BinaryResponse> {
  Class<R> response();

  Object serialize();

  default boolean validate(@NotNull R response) {
    return true;
  }

  default boolean shouldBeAllowed(@NotNull TabNineInvalidResponseException e) {
    return false;
  }
}

