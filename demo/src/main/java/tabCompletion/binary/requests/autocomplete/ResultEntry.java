package tabCompletion.binary.requests.autocomplete;

import tabCompletion.binary.requests.autocomplete.CompletionMetadata;
import tabCompletion.general.CompletionKind;
import tabCompletion.intellij.completions.Completion;

public class ResultEntry implements Completion {
  public String new_prefix;
  public String old_suffix;
  public String new_suffix;
  public CompletionMetadata completion_metadata;

  @Override
  public boolean isSnippet() {
    if (this.completion_metadata == null) {
      return false;
    }

    return this.completion_metadata.getCompletion_kind() == CompletionKind.Snippet;
  }
}
