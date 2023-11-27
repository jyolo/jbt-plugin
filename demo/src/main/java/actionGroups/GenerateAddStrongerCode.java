package actionGroups;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.common.ChatGPTBundle;
import org.jetbrains.annotations.NotNull;

public class GenerateAddStrongerCode extends AbstractEditorAction {

    public GenerateAddStrongerCode() {
        super(() -> ChatGPTBundle.message("action.code.generateAddStrongerCode.menu"));
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        key = ChatGPTBundle.message("action.code.generateAddStrongerCode.title");
        super.customActionPerformed(e, GENERATE_ADD_STRONGER_CODE, "");
    }

}
