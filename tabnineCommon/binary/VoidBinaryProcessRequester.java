package com.tabnineCommon.binary;

import com.tabnineCommon.binary.exceptions.TabNineDeadException;
import org.jetbrains.annotations.Nullable;

public class VoidBinaryProcessRequester implements BinaryProcessRequester {
  private static final BinaryProcessRequester INSTANCE = new VoidBinaryProcessRequester();

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
