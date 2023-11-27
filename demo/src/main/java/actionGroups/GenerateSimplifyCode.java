package actionGroups;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.common.ChatGPTBundle;
import org.jetbrains.annotations.NotNull;

public class GenerateSimplifyCode extends AbstractEditorAction {

    public GenerateSimplifyCode() {
        super(() -> ChatGPTBundle.message("action.code.generateSimplifyCode.menu"));
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        key = ChatGPTBundle.message("action.code.generateSimplifyCode.title");
        super.customActionPerformed(e, GENERATE_SIMPLIFY_CODE, "");

    }


}
