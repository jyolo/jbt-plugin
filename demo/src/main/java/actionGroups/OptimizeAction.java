package actionGroups;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.common.ChatGPTBundle;
import settings.OpenAISettingsState;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
public class OptimizeAction extends AbstractEditorAction {

    public OptimizeAction() {
        super(() -> ChatGPTBundle.message("action.code.optimize.menu"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("OptimizeAction________________");
        key = ChatGPTBundle.message("action.code.optimize.title");
        OpenAISettingsState instance = OpenAISettingsState.getInstance();
        String customInstructions = instance.optimizeInstructions;
        super.customActionPerformed(e, ACTION_OPTIMIZE, customInstructions);
    }

}
