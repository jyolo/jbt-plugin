package tabCompletion.binary.fetch;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.text.SemVer;
import tabCompletion.binary.fetch.BinaryVersion;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static tabCompletion.general.StaticConfig.getActiveVersionPath;
import static tabCompletion.general.StaticConfig.getBaseDirectory;
import static java.util.stream.Collectors.toList;

public class LocalBinaryVersions {
  public static final String BAD_VERSION = "4.0.47";
  private BinaryValidator binaryValidator;

  public LocalBinaryVersions(BinaryValidator binaryValidator) {
    this.binaryValidator = binaryValidator;
  }

  @NotNull
  public List<tabCompletion.binary.fetch.BinaryVersion> listExisting() {
    File[] versionsFolders =
        Optional.ofNullable(getBaseDirectory().toFile().listFiles()).orElse(new File[0]);

    return Stream.of(versionsFolders)
        .map(File::getName)
        .map(SemVer::parseFromText)
        .filter(Objects::nonNull)
        .sorted(Comparator.reverseOrder())
        .map(SemVer::toString)
        .map(tabCompletion.binary.fetch.BinaryVersion::new)
        .filter(
            version ->
                !version.getVersion().equals(BAD_VERSION)
                    && binaryValidator.isWorking(version.getVersionFullPath()))
        .collect(toList());
  }

  public Optional<tabCompletion.binary.fetch.BinaryVersion> activeVersion() {
    List<String> lines = readActiveFile();

    if (lines.size() == 0) return Optional.empty();

    String version = lines.get(0);

    if (version.equals(BAD_VERSION)) return Optional.empty();

    tabCompletion.binary.fetch.BinaryVersion binaryVersion = new BinaryVersion(version);

    if (!binaryValidator.isWorking(binaryVersion.getVersionFullPath())) {
      Logger.getInstance(getClass()).warn("Version in .active file is not working");
      return Optional.empty();
    }

    return Optional.of(binaryVersion);
  }

  @NotNull
  private List<String> readActiveFile() {
    Path activeFile = getActiveVersionPath();

    List<String> lines = new ArrayList<>();
    if (activeFile.toFile().exists()) {
      try {
        lines = Files.readAllLines(activeFile);
      } catch (IOException e) {
        Logger.getInstance(getClass()).warn("Failed to read .active file", e);
      }
    }

    return lines;
  }
}
