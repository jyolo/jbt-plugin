package com.tabnineCommon.binary;

import static com.tabnineCommon.general.StaticConfig.COMPLETION_TIME_THRESHOLD;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.tabnine.AIHttpHelper;
import com.tabnineCommon.binary.exceptions.TabNineDeadException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.jetbrains.annotations.Nullable;

public class BinaryRequestFacade {
  private final BinaryProcessRequesterProvider binaryProcessRequesterProvider;

  public BinaryRequestFacade(BinaryProcessRequesterProvider binaryProcessRequesterProvider) {
    this.binaryProcessRequesterProvider = binaryProcessRequesterProvider;
  }

  public Long pid() {
    return binaryProcessRequesterProvider.get().pid();
  }

  public <R extends BinaryResponse> R executeRequest(BinaryRequest<R> req) {
    return executeRequest(req, COMPLETION_TIME_THRESHOLD);
  }

  @Nullable
  public <R extends BinaryResponse> R executeRequest(BinaryRequest<R> req, int timeoutMillis) {
    AIHttpHelper aiHttpHelper = new AIHttpHelper();
    try {
      R result =
              AppExecutorUtil.getAppExecutorService()
                      .submit(() -> aiHttpHelper.request(req))
                      .get(timeoutMillis, TimeUnit.MILLISECONDS);
      if (result != null) {
        binaryProcessRequesterProvider.onSuccessfulRequest();
      }
      return result;
    } catch (TimeoutException e) {
      e.printStackTrace();
      binaryProcessRequesterProvider.onTimeout();
    } catch (ExecutionException e) {
      e.printStackTrace();
      if (e.getCause() instanceof TabNineDeadException) {
        binaryProcessRequesterProvider.onDead(e.getCause());
      } else {
        Logger.getInstance(getClass()).warn("Tabnine's threw an unknown error during request.", e);
      }
    } catch (CancellationException e) {
      // This is ok. Nothing needs to be done.
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
      Logger.getInstance(getClass()).warn("Tabnine's threw an unknown error.", e);
    }

    return null;
  }
}
