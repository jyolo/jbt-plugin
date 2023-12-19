package tabCompletion.capabilities;

import com.google.gson.Gson;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBus;
import tabCompletion.binary.BinaryRequestFacade;
import tabCompletion.binary.requests.capabilities.CapabilitiesResponse;
import tabCompletion.capabilities.Capabilities;
import tabCompletion.capabilities.Capability;
import tabCompletion.config.Config;
import tabCompletion.general.DependencyContainer;
import tabCompletion.lifecycle.CapabilitiesStateSingleton;

import java.util.*;

public class CapabilitiesService {

  public static final int LOOP_INTERVAL_MS = 1000;

  public static final int REFRESH_EVERY_MS = 10 * 1000; // 10 secs

  public static final Gson GSON = new Gson();

  private Thread refreshLoop = null;
  private final MessageBus messageBus = ApplicationManager.getApplication().getMessageBus();

  private final BinaryRequestFacade binaryRequestFacade =
      DependencyContainer.instanceOfBinaryRequestFacade();

  public static CapabilitiesService getInstance() {
    return ServiceManager.getService(CapabilitiesService.class);
  }

  public void init() {
    scheduleFetchCapabilitiesTask();
  }

  public boolean isCapabilityEnabled(Capability capability) {
    if (Config.IS_SELF_HOSTED) {
      return true;
    }

    return CapabilitiesStateSingleton.getInstance()
        .getOptional()
        .map(c -> c.isEnabled(capability))
        .orElse(false);
  }

  public boolean isReady() {
    return CapabilitiesStateSingleton.getInstance()
        .getOptional()
        .map(Capabilities::isReady)
        .orElse(false);
  }

//  public void forceRefreshCapabilities() {
////    binaryRequestFacade.executeRequest(new RefreshRemotePropertiesRequest());
////    fetchCapabilities();
////  }

  private synchronized void scheduleFetchCapabilitiesTask() {
    if (refreshLoop == null) {
      refreshLoop = new Thread(this::fetchCapabilitiesLoop);
      refreshLoop.setDaemon(true);
      refreshLoop.start();
    }
  }

  private void fetchCapabilitiesLoop() {
    Optional<Long> lastRefresh = Optional.empty();
    Optional<Long> lastPid = Optional.empty();

    try {
      while (true) {
        try {
          Long pid = binaryRequestFacade.pid();
          boolean expiredSinceLastRefresh =
              !lastRefresh.isPresent()
                  || System.currentTimeMillis() - lastRefresh.get() >= REFRESH_EVERY_MS;

          boolean pidChanged =
              !lastPid.isPresent() || lastPid.get() == null || !lastPid.get().equals(pid);

          if (expiredSinceLastRefresh || pidChanged) {
            fetchCapabilities();

            lastRefresh = Optional.of(System.currentTimeMillis());
            lastPid = Optional.of(pid);
          }
        } catch (Throwable t) {
          Logger.getInstance(getClass()).debug("Unexpected error. Capabilities refresh failed", t);
        }

        Thread.sleep(LOOP_INTERVAL_MS);
      }
    } catch (Throwable t) {
      Logger.getInstance(getClass()).warn("Unexpected error. Capabilities refresh loop exiting", t);
    }
  }

  private void fetchCapabilities() {
//    final CapabilitiesResponse capabilitiesResponse =
//        binaryRequestFacade.executeRequest(new CapabilitiesRequest());
    final CapabilitiesResponse capabilitiesResponse = getFeignCapabilitiesResponse();
    if (capabilitiesResponse != null && capabilitiesResponse.getEnabledFeatures() != null) {
      setCapabilities(capabilitiesResponse);
    }
  }

  private CapabilitiesResponse getFeignCapabilitiesResponse() {
    CapabilitiesResponse capabilitiesResponse = new CapabilitiesResponse();
    List<Capability> list = new ArrayList<>();
    list.add(Capability.INLINE_SUGGESTIONS);
    capabilitiesResponse.setEnabledFeatures(list);
    return capabilitiesResponse;
  }

  private void setCapabilities(CapabilitiesResponse capabilitiesResponse) {
    Set<Capability> newCapabilities = new HashSet<>();
    List<Capability> capabilities = capabilitiesResponse.getEnabledFeatures();

    if (capabilities != null) {
      capabilities.stream().filter(Objects::nonNull).forEach(newCapabilities::add);
    }

    Capabilities newCapabilitiesState =
        new Capabilities(newCapabilities, capabilitiesResponse.getExperimentSource());

    CapabilitiesStateSingleton.getInstance().set(newCapabilitiesState);
  }
}
