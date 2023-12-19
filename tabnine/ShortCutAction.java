package com.tabnine;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.tabnineCommon.general.EditorUtils;
import com.tabnineCommon.inline.CompletionPreview;
import com.tabnineCommon.inline.DefaultCompletionAdjustment;
import com.tabnineCommon.inline.InlineCompletionCache;
import com.tabnineCommon.inline.InlineCompletionHandler;
import com.tabnineCommon.prediction.TabNineCompletion;
import com.tabnineCommon.state.CompletionsState;
import org.jetbrains.annotations.NotNull;

import static com.tabnineCommon.general.DependencyContainer.singletonOfInlineCompletionHandler;


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
