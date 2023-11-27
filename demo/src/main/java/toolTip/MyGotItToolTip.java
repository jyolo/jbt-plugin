package toolTip;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.ui.JBColor;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;


import static com.common.util.wrapWithHtml;
import static com.common.util.wrapWithHtmlTag;

public class MyGotItToolTip extends GotItTooltip{
    private final String tooltipId;
    private final String tooltipHeader;
    private final String tooltipBody;
    private final GotItTooltipAction gotItTooltipAction;
    private boolean isVisible = false;
    private Balloon tooltip;
    private String buttonText = "快捷问答 ALT+I";
    private static final int POINT_DX = 5;

    public MyGotItToolTip(String tooltipId, String tooltipHeader, String tooltipBody, GotItTooltipAction gotItTooltipAction) {
        super(tooltipId, tooltipHeader, tooltipBody, gotItTooltipAction);
        this.tooltipId = tooltipId;
        this.tooltipHeader = wrapWithHtml(wrapWithHtmlTag(tooltipHeader, "h3"));
        this.tooltipBody = wrapWithHtml(tooltipBody);
        this.gotItTooltipAction = gotItTooltipAction;
    }


    public void show(Editor editor) {
        ApplicationManager.getApplication()
                .invokeLater(
                        () -> {
                            this.isVisible = true;
                            JButton gotItButton = new JButton(buttonText);
                            tooltip = createBalloon(createTooltipContent(gotItButton, tooltipHeader, tooltipBody));
                            tooltip.setAnimationEnabled(false);
                            tooltip.addListener(
                                    new JBPopupListener() {
                                        @Override
                                        public void beforeShown(@NotNull LightweightWindowEvent event) {
                                            System.out.println("beforeShown");
//                                            binaryRequestFacade.executeRequest(
//                                                    new HintShownRequest(tooltipId, tooltipHeader, null, null));
                                        }
                                        @Override
                                        public void onClosed(@NotNull LightweightWindowEvent event){
                                            System.out.println("closed");
                                        }
                                    });
                            showTooltip(editor, tooltip);
                            gotItButton.addActionListener(e -> {
                                tooltip.hide(true);
                                gotItTooltipAction.onGotItClicked();
                            });
                        });
    }

    private JPanel createTooltipContent(
            JButton gotItButton, String tooltipHeader, String tooltipBody) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(JBColor.background());
        panel.add(gotItButton);

        return panel;
    }

    private void showTooltip(Editor editor, Balloon tooltip) {
        RelativePoint relativePoint = JBPopupFactory.getInstance().guessBestPopupLocation(editor);

        int selectionStart = editor.getSelectionModel().getSelectionStart();
        int lineNumber = editor.getDocument().getLineNumber(selectionStart);
        if(lineNumber <= 3){
            relativePoint.getPoint().translate(POINT_DX, POINT_DX);
            tooltip.show(relativePoint, Balloon.Position.below);
        }else{
            relativePoint.getPoint().translate(POINT_DX, -editor.getLineHeight() - POINT_DX);
            tooltip.show(relativePoint, Balloon.Position.above);
        }

    }

    private Balloon createBalloon(JPanel content) {
        return JBPopupFactory.getInstance()
                .createBalloonBuilder(content)
                .setBorderInsets(JBUI.insets(2, 2, 2, 2))
                .setFillColor(JBColor.background())
                .setHideOnKeyOutside(true)
                .setFadeoutTime(0)
                .setAnimationCycle(0)
                .setHideOnAction(true)
                .createBalloon();
    }
}
