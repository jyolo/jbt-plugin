package com.obiscr.chatgpt.ui.action.editor;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.obiscr.chatgpt.message.ChatGPTBundle;
import org.jetbrains.annotations.NotNull;

public class GeneratePickCommonFunc extends AbstractEditorAction {

    public GeneratePickCommonFunc() {
        super(() -> ChatGPTBundle.message("action.code.generatePickCommonFunc.menu"));
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        key = ChatGPTBundle.message("action.code.generatePickCommonFunc.title");
        super.customActionPerformed(e, GENERATE_PICK_COMMON_FUNC, "");
    }

}
