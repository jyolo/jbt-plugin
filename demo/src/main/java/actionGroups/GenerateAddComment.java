package com.obiscr.chatgpt.ui.action.editor;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.obiscr.chatgpt.message.ChatGPTBundle;
import org.jetbrains.annotations.NotNull;

public class GenerateAddComment extends AbstractEditorAction {

    public GenerateAddComment() {
        super(() -> ChatGPTBundle.message("action.code.generateAddComment.menu"));
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        key = ChatGPTBundle.message("action.code.generateAddComment.title");
        super.customActionPerformed(e, GENERATE_ADD_COMMENT, "");
    }

}
