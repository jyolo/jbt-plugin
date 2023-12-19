package statusBar;

import com.intellij.ide.DataManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.impl.status.EditorBasedWidget;
import com.intellij.util.Consumer;
import statusBar.StatusBarActions;
import statusBar.StatusBarEmptySymbolGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseEvent;

import static statusBar.SubscriptionTypeKt.getSubscriptionType;
import static tabCompletion.general.StaticConfig.LIMITATION_SYMBOL;

public class AIReviewStatusBarWidget extends EditorBasedWidget
        implements StatusBarWidget, StatusBarWidget.MultipleTextValuesPresentation {
    private final StatusBarEmptySymbolGenerator emptySymbolGenerator =
            new StatusBarEmptySymbolGenerator();
    private boolean isLimited = false;

    public AIReviewStatusBarWidget(@NotNull Project project) {
        super(project);
        // register for state changes (we will get notified whenever the state changes)
    }

    public Icon getIcon() {
        return getSubscriptionType().getTabnineLogo();
    }

    public @Nullable("null means the widget is unable to show the popup") ListPopup getPopupStep() {
        return createPopup();
    }

    public String getSelectedValue() {
        return this.isLimited ? LIMITATION_SYMBOL : emptySymbolGenerator.getEmptySymbol();
    }

    // Compatability implementation. DO NOT ADD @Override.
    @Nullable
    public WidgetPresentation getPresentation() {
        return this;
    }

    @NotNull
    @Override
    public String ID() {
        return getClass().getName();
    }

    private ListPopup createPopup() {
//    ListPopup popup =
//        JBPopupFactory.getInstance()
//            .createActionGroupPopup(
//                null,
//                StatusBarActions.buildStatusBarActionsGroup(myStatusBar, ID()),
//                DataManager.getInstance()
//                    .getDataContext(myStatusBar != null ? myStatusBar.getComponent() : null),
//                JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
//                true);
//    return popup;
        return null;
    }

    // Compatability implementation. DO NOT ADD @Override.
    @Nullable
    public String getTooltipText() {
//    return "AI Review (Click to open settings)";
        return "";
    }

    // Compatability implementation. DO NOT ADD @Override.
    @Nullable
    public Consumer<MouseEvent> getClickConsumer() {
        return null;
    }

    public void update() {
        if (myStatusBar == null) {
            Logger.getInstance(getClass()).warn("Failed to update the status bar");
            return;
        }
        myStatusBar.updateWidget(ID());
    }
}
