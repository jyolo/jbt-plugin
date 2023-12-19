package ideActions;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorKind;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.wm.IdeFocusManager;
import org.jetbrains.annotations.NotNull;
import java.awt.*;
import static com.intellij.openapi.editor.EditorModificationUtil.checkModificationAllowed;

import tabCompletion.inline.*;
import tabCompletion.prediction.TabNineCompletion;
import static tabCompletion.general.DependencyContainer.singletonOfInlineCompletionHandler;


public class ChangeDocumentListener implements DocumentListener {
    private final InlineCompletionHandler handler = singletonOfInlineCompletionHandler();
    @Override
    public void documentChanged(DocumentEvent event) {

//        System.out.println("documentChanged");
//
//        Document document = event.getDocument();
//        Editor editor = getActiveEditor(document);
//
//        TabNineCompletion lastShownCompletion = CompletionPreview.getCurrentCompletion(editor);
//        CompletionPreview.clear(editor);
//        int offset = event.getOffset() + event.getNewLength();
//        Boolean shouldIgnoreChange = shouldIgnoreChange(event, editor, offset, lastShownCompletion);
//        System.out.println("------------documentChangedNonBulk------------");
//        System.out.println(lastShownCompletion);
//        System.out.println(offset);
//        System.out.println(event.getOffset());
//        System.out.println(event.getNewLength());
//        System.out.println(shouldIgnoreChange);
//        System.out.println("------------documentChangedNonBulk------------");
//        if (shouldIgnoreChange) {
//            InlineCompletionCache.getInstance().clear(editor);
//            return;
//        }
//
//        handler.retrieveAndShowCompletion(
//                editor,
//                offset,
//                lastShownCompletion,
//                event.getNewFragment().toString(),
//                new DefaultCompletionAdjustment());

    }

    private static Editor getActiveEditor(@NotNull Document document) {
        if (!ApplicationManager.getApplication().isDispatchThread()) {
            return null;
        }

        Component focusOwner = IdeFocusManager.getGlobalInstance().getFocusOwner();
        DataContext dataContext = DataManager.getInstance().getDataContext(focusOwner);
        // ignore caret placing when exiting
        Editor activeEditor =
                ApplicationManager.getApplication().isDisposed()
                        ? null
                        : CommonDataKeys.EDITOR.getData(dataContext);

        if (activeEditor != null && activeEditor.getDocument() != document) {
            activeEditor = null;
        }

        return activeEditor;
    }


    private boolean shouldIgnoreChange(
            DocumentEvent event, Editor editor, int offset, TabNineCompletion lastShownCompletion) {
        Document document = event.getDocument();

//        if (!suggestionsModeService.getSuggestionMode().isInlineEnabled()) {
//            return true;
//        }

        if (event.getNewLength() < 1) {
            return true;
        }

        if (!editor.getEditorKind().equals(EditorKind.MAIN_EDITOR)
                && !ApplicationManager.getApplication().isUnitTestMode()) {
            return true;
        }

        if (!checkModificationAllowed(editor) || document.getRangeGuard(offset, offset) != null) {
            document.fireReadOnlyModificationAttempt();

            return true;
        }

        return !CompletionUtils.isValidDocumentChange(document, offset, event.getOffset());
    }

}
