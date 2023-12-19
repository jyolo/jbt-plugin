package tabCompletion.selections;

import com.intellij.openapi.editor.Editor;

public interface CompletionListener {
  void onCompletion(Editor editor);
}
