package tabCompletion.binary.fetch;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.util.text.SemVer;
import tabCompletion.binary.fetch.BinaryVersion;
import tabCompletion.binary.fetch.BinaryVersionFetcher;
import tabCompletion.general.StaticConfig;
import tabCompletion.lifecycle.PluginInstalled;

import java.util.Optional;
import java.util.prefs.Preferences;

// bootstrapper support class
// once out of beta/alpha all of this code can replace the existing fetchBinary()
public class BootstrapperSupport {
  public static final String BOOTSTRAPPED_VERSION_KEY = "bootstrapped version";

  static Optional<tabCompletion.binary.fetch.BinaryVersion> bootstrapVersion(
      LocalBinaryVersions localBinaryVersions,
      BinaryRemoteSource binaryRemoteSource,
      BundleDownloader bundleDownloader) {
    Optional<tabCompletion.binary.fetch.BinaryVersion> localBootstrapVersion =
        locateLocalBootstrapSupportedVersion(localBinaryVersions);
    if (localBootstrapVersion.isPresent()) {
      PluginInstalled.Companion.setNewInstallation(false);

      if (!BinaryVersionFetcher.isBadVersion(localBootstrapVersion.get())) {
        return localBootstrapVersion;
      }
    }

    if (!localBootstrapVersion.isPresent()) {
      notifyPluginInstalled();
    }

    return downloadRemoteVersion(binaryRemoteSource, bundleDownloader);
  }

  private static Preferences getPrefs() {
    return Preferences.userNodeForPackage(BootstrapperSupport.class);
  }

  private static tabCompletion.binary.fetch.BinaryVersion savePreferredBootstrapVersion(tabCompletion.binary.fetch.BinaryVersion version) {
    getPrefs().put(BOOTSTRAPPED_VERSION_KEY, version.getVersion());
    return version;
  }

  private static Optional<SemVer> minimalBootstrappedVersion() {
    return Optional.ofNullable(getPrefs().get(BOOTSTRAPPED_VERSION_KEY, null))
        .map(SemVer::parseFromText);
  }

  private static Optional<tabCompletion.binary.fetch.BinaryVersion> locateLocalBootstrapSupportedVersion(
      LocalBinaryVersions localBinaryVersions) {
    return minimalBootstrappedVersion()
        .flatMap(
            version -> {
              Optional<tabCompletion.binary.fetch.BinaryVersion> activeVersion = localBinaryVersions.activeVersion();
              if (activeVersion.isPresent()) return activeVersion;

              return localBinaryVersions.listExisting().stream()
                  .filter(v -> SemVer.parseFromText(v.getVersion()) != null)
                  .filter(v -> SemVer.parseFromText(v.getVersion()).compareTo(version) >= 0)
                  .findFirst();
            });
  }

  private static Optional<BinaryVersion> downloadRemoteVersion(
          BinaryRemoteSource binaryRemoteSource, BundleDownloader bundleDownloader) {
    Optional<String> serverUrl = StaticConfig.getTabNineBundleVersionUrl();
    return serverUrl.flatMap(
        s ->
            binaryRemoteSource
                .fetchPreferredVersion(s)
                .flatMap(bundleDownloader::downloadAndExtractBundle)
                .map(BootstrapperSupport::savePreferredBootstrapVersion));
  }

  private static void notifyPluginInstalled() {
    if (ApplicationManager.getApplication() != null) {
      PluginInstalled.Companion.setNewInstallation(true);
    }
  }
}
