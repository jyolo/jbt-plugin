package actionGroups;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.PsiFile;
import com.common.ChatGPTBundle;
import settings.OpenAISettingsState;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
public class TestCaseAction extends AbstractEditorAction {

    public TestCaseAction() {
        super(() -> ChatGPTBundle.message("action.code.test.menu"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        key = ChatGPTBundle.message("action.code.test.title");
        OpenAISettingsState instance = OpenAISettingsState.getInstance();
        String customInstructions = instance.addTestsInstructions;
        super.customActionPerformed(e, ACTION_ADD_TESTS, customInstructions);
    }

}
