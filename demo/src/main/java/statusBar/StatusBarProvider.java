package statusBar;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidgetProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StatusBarProvider implements StatusBarWidgetProvider {
    @Nullable
    @Override
    public com.intellij.openapi.wm.StatusBarWidget getWidget(@NotNull Project project) {
        return new AIReviewStatusBarWidget(project);
    }

    @NotNull
    @Override
    public String getAnchor() {
        return StatusBar.Anchors.before(StatusBar.StandardWidgets.POSITION_PANEL);
    }
}
