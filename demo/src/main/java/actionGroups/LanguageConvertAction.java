package actionGroups;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.common.ChatGPTBundle;
import settings.OpenAISettingsState;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
public class LanguageConvertAction extends AbstractEditorAction {

    public LanguageConvertAction() {
        super(() -> ChatGPTBundle.message("action.code.languageConvert.menu"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        key = ChatGPTBundle.message("action.code.languageConvert.title");
        OpenAISettingsState instance = OpenAISettingsState.getInstance();
        String languageConvertInstructions = instance.languageConvertInstructions;
        super.customActionPerformed(e, ACTION_CONVERT, languageConvertInstructions);
    }

}
