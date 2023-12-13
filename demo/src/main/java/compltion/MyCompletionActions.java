package compltion;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.CompletionContributor;
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

        CompletionContributor obj = new TabNineCompletionContributor();
        obj.fillCompletionVariants();
    }
}