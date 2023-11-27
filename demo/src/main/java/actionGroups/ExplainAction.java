package actionGroups;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.common.ChatGPTBundle;
import settings.OpenAISettingsState;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
public class ExplainAction extends AbstractEditorAction {

    public ExplainAction() {
        super(() -> ChatGPTBundle.message("action.code.explain.menu"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        key = ChatGPTBundle.message("action.code.explain.title");
        OpenAISettingsState instance = OpenAISettingsState.getInstance();
        String customInstructions = instance.explainInstructions;
        super.customActionPerformed(e, ACTION_EXPLAIN, customInstructions);
    }

}
