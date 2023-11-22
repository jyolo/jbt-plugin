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
public class QianLiuPanel implements Configurable, Disposable {
    private JPanel myMainPanel;
    private JPanel apiKeyTitledBorderBox;
    private JBTextField apiKeyField;


    public QianLiuPanel() {
        init();
    }

    private void init() {
        apiKeyField.getEmptyText().setText(ChatGPTBundle.message("system.setting.token.text"));
    }



    @Override
    public void reset() {
        OpenAISettingsState state = OpenAISettingsState.getInstance();
        apiKeyField.setText(state.apiKey);
    }

    @Override
    public @Nullable JComponent createComponent() {
        return myMainPanel;
    }

    @Override
    public boolean isModified() {
        OpenAISettingsState state = OpenAISettingsState.getInstance();

        return !state.apiKey.equals(apiKeyField.getText());
    }

    @Override
    public void apply() {
        OpenAISettingsState state = OpenAISettingsState.getInstance();
        state.apiKey = apiKeyField.getText();
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
