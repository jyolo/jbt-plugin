package actionGroups;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.common.ChatGPTBundle;
import settings.OpenAISettingsState;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
public class AiChatAction extends AbstractEditorAction {

    public AiChatAction() {
        super(() -> ChatGPTBundle.message("action.code.AiChat.menu"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        key = ChatGPTBundle.message("action.code.AiChat.title");
        OpenAISettingsState instance = OpenAISettingsState.getInstance();
        String customInstructions = instance.explainInstructions;
        super.customActionPerformed(e, ACTION_AI_CHAT, customInstructions);
    }

}
