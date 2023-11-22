package setting;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "com.aireview.settings.AiReviewSettingState",
        storages = @Storage("AiReviewSettingsPlugin.xml")
)
public class AiReviewSettingsState implements PersistentStateComponent<AiReviewSettingsState> {

    public static String toolWindowName = "千流AI";
    public Boolean enableAiReview = false;
    public String shortcutKeys = "alt pressed R";
    public Integer maxDelayTime = 2000;
    public Integer delayTime = 10;
    public Boolean reviewIng = false;


    public static AiReviewSettingsState getInstance(){
        return ApplicationManager.getApplication().getService(AiReviewSettingsState.class);
    }

    @Nullable
    @Override
    public AiReviewSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AiReviewSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }


}
