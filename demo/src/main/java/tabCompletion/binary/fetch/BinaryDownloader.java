package tabCompletion.binary.fetch;

import tabCompletion.binary.fetch.BinaryVersion;
import tabCompletion.general.StaticConfig;

import java.util.Optional;

import static tabCompletion.general.StaticConfig.*;

public class BinaryDownloader {
  private final TempBinaryValidator tempBinaryValidator;
  private final GeneralDownloader downloader;

  public BinaryDownloader(TempBinaryValidator tempBinaryValidator, GeneralDownloader downloader) {
    this.tempBinaryValidator = tempBinaryValidator;
    this.downloader = downloader;
  }

  public Optional<tabCompletion.binary.fetch.BinaryVersion> downloadBinary(String version) {
    Optional<String> serverUrl = StaticConfig.getBundleServerUrl();
    if (!serverUrl.isPresent()) {
      return Optional.empty();
    }

    String urlString = String.join("/", serverUrl.get(), version, TARGET_NAME, EXECUTABLE_NAME);
    String destination = versionFullPath(version);
    if (this.downloader.download(urlString, destination, tempBinaryValidator)) {
      return Optional.of(new BinaryVersion(destination, version));
    }
    return Optional.empty();
  }
}
