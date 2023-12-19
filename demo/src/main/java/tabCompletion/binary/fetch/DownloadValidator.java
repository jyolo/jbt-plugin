package tabCompletion.binary.fetch;

import tabCompletion.binary.exceptions.FailedToDownloadException;

import java.nio.file.Path;

public interface DownloadValidator {
  void validateAndRename(Path tempDestination, Path destination) throws FailedToDownloadException;
}
