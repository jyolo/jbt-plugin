package tabCompletion;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import tabCompletion.general.EditorUtils;
import tabCompletion.inline.CompletionPreview;
import tabCompletion.inline.DefaultCompletionAdjustment;
import tabCompletion.inline.InlineCompletionHandler;
import tabCompletion.prediction.TabNineCompletion;
import tabCompletion.state.CompletionsState;
import org.jetbrains.annotations.NotNull;

import static tabCompletion.general.DependencyContainer.singletonOfInlineCompletionHandler;


public class ShortCutAction extends AnAction {
    private final InlineCompletionHandler handler = singletonOfInlineCompletionHandler();
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        if (!CompletionsState.INSTANCE.isCompletionsEnabled()) {
            return;
        }

        Editor editor = FileEditorManager.getInstance(e.getProject()).getSelectedTextEditor();

        if (editor == null || !EditorUtils.isMainEditor(editor)) {
            return;
        }



        TabNineCompletion lastShownCompletion = CompletionPreview.getCurrentCompletion(editor);

        CompletionPreview.clear(editor);

        int offset = editor.getCaretModel().getOffset();

//        if (shouldIgnoreChange(event, editor, offset, lastShownCompletion)) {
//            InlineCompletionCache.getInstance().clear(editor);
//            return;
//        }

        handler.retrieveAndShowCompletion(
                editor,
                offset,
                lastShownCompletion,
                "",
                new DefaultCompletionAdjustment());
    }
}
