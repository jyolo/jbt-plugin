// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBTextField;
import com.common.ChatGPTBundle;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * @author Wuzi
 */
public class GPT3_35_TurboPanel implements Configurable, Disposable {
    private JPanel myMainPanel;
    private JPanel apiKeyTitledBorderBox;
    //    private JBTextField optimizeInstructionsField;
    private JBTextField addTestsInstructionsField;
    private JBTextField explainInstructionsField;
    private JBTextField findProblemsInstructionsField;
    private JBTextField languageConvertInstructionsField;
    private JBTextField AICodeReviewField;


    public GPT3_35_TurboPanel() {
        init();
    }

    private void init() {
        addTestsInstructionsField.getEmptyText().setText(ChatGPTBundle.message("system.setting.add_tests_instructions.text"));
        explainInstructionsField.getEmptyText().setText(ChatGPTBundle.message("system.setting.explain_instructions.text"));
//        findProblemsInstructionsField.getEmptyText().setText(ChatGPTBundle.message("system.setting.find_problems_instructions.text"));
//        languageConvertInstructionsField.getEmptyText().setText(ChatGPTBundle.message("system.setting.language_convert_instructions.text"));
        AICodeReviewField.getEmptyText().setText(ChatGPTBundle.message("system.setting.ai_code_review.text"));
    }



    @Override
    public void reset() {
        System.out.println("reset__________");
        OpenAISettingsState state = OpenAISettingsState.getInstance();
        addTestsInstructionsField.setText(state.addTestsInstructions);
        explainInstructionsField.setText(state.explainInstructions);
        languageConvertInstructionsField.setText(state.languageConvertInstructions);
        AICodeReviewField.setText(state.AICodeReviewInstructions);
    }

    @Override
    public @Nullable JComponent createComponent() {
        return myMainPanel;
    }

    @Override
    public boolean isModified() {
        OpenAISettingsState state = OpenAISettingsState.getInstance();

        return !state.addTestsInstructions.equals(addTestsInstructionsField.getText()) ||
                !state.explainInstructions.equals(explainInstructionsField.getText()) ||
                !state.languageConvertInstructions.equals(languageConvertInstructionsField.getText()) ||
                !state.AICodeReviewInstructions.equals(AICodeReviewField.getText());
    }

    @Override
    public void apply() {
        OpenAISettingsState state = OpenAISettingsState.getInstance();

        state.addTestsInstructions = addTestsInstructionsField.getText();
        state.explainInstructions = explainInstructionsField.getText();
        state.languageConvertInstructions = languageConvertInstructionsField.getText();
        state.AICodeReviewInstructions = AICodeReviewField.getText();
        System.out.println("AICodeReviewField.getText()" + AICodeReviewField.getText());

    }

    @Override
    public void dispose() {
    }

    @Override
    public String getDisplayName() {
        return ChatGPTBundle.message("ui.setting.menu.text");
    }

    private void createUIComponents() {
        apiKeyTitledBorderBox = new JPanel(new BorderLayout());
        TitledSeparator tsUrl = new TitledSeparator(ChatGPTBundle.message("system.setting.token.set"));
        apiKeyTitledBorderBox.add(tsUrl,BorderLayout.CENTER);
    }
}
