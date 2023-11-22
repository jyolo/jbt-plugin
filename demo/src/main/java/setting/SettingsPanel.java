package setting;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.TitledSeparator;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel implements Configurable, Disposable {

    AiReviewSettingsState settingsState = AiReviewSettingsState.getInstance();
    private JPanel mainPanel;
    private JLabel subTitle;
    private JPanel titledBorderBox;
    private JCheckBox enableAiReview;
    private JTextField delayTime;
    private JFormattedTextField shortcutKeys;
    public String getDisplayName() {
        return "千流AI代码Review";
    }

    @Override
    public void dispose() {}

    @Override
    public @Nullable JComponent createComponent() {
        delayTime.setText(String.valueOf(settingsState.delayTime));
        enableAiReview.setSelected(settingsState.enableAiReview);

        shortcutKeys.setEditable(false);
        return mainPanel;
    }

    @Override
    public boolean isModified() {
        boolean modified = false;
        modified = checkInteger(settingsState.delayTime, delayTime);
        if (modified){
            return modified;
        }

        modified = checkBoolean(settingsState.enableAiReview, enableAiReview);
        if (modified){
            return modified;
        }
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        settingsState.enableAiReview = enableAiReview.isSelected();
        settingsState.delayTime = Integer.valueOf(delayTime.getText());
        if (settingsState.delayTime < 2) {
            settingsState.delayTime = 2;
        } else if (settingsState.delayTime > 1440) {
            settingsState.delayTime = 1440;
        }
    }

    private boolean checkBoolean(boolean settingsParams, JCheckBox formParams) {
        /**
         * is equals
         */
        if (formParams != null) {
            return formParams.isSelected() != settingsParams;
        }
        return false;
    }

    private boolean checkInteger(long settingsParams, JTextField formParams) {
        /**
         * is equals
         */
        String text = formParams.getText();
        if (text == null || text.isEmpty()) {
            return false;
        }
        try{
            if (Integer.valueOf(text) == settingsParams){
                return false;
            }
        }catch (NumberFormatException e){
            return false;
        }

        return true;
    }

    private void createUIComponents() {
        titledBorderBox = new JPanel(new BorderLayout());
        TitledSeparator tsUrl = new TitledSeparator("千流AI代码Review");
        titledBorderBox.add(tsUrl,BorderLayout.CENTER);
    }
}
