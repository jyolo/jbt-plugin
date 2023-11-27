package actionGroups;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.common.ChatGPTBundle;
import org.jetbrains.annotations.NotNull;

public class GenerateAddDebugCode extends AbstractEditorAction {

    public GenerateAddDebugCode() {
        super(() -> ChatGPTBundle.message("action.code.generateAddDebugCcde.menu"));
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        key = ChatGPTBundle.message("action.code.generateAddDebugCcde.title");
        super.customActionPerformed(e, GENERATE_ADD_DEBUG_CODE, "");
    }

}
