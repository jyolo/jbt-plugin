package compltion;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

class MyCompletionActions extends AnAction {
    public MyCompletionActions() {
        super("MyCompletionActions");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Editor editor = e.getData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR);
        final Project project = e.getProject();

        if (editor == null || project == null) {
            return;
        }

        // 在插入 "hello" 前可以选择是否需要删除行内已存在的部分词或字母，通过 editor.getDocument().deleteString(startOffset, endOffset) 方法来实现。

        // 向文档中插入 "hello"
        int offset = editor.getCaretModel().getOffset();
        editor.getDocument().insertString(offset, "hello");

        // 移动光标到插入文字之后，以便补全可以在正确位置出现
        editor.getCaretModel().moveToOffset(offset + "hello".length());

        // 触发自动补全弹窗
        AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null);
    }
}