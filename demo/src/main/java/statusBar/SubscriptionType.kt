package statusBar

import com.intellij.openapi.util.IconLoader
import com.intellij.ui.AnimatedIcon
import com.intellij.util.IconUtil
import com.intellij.util.containers.ContainerUtil
import settings.AiReviewSettingsState
import javax.swing.Icon
import javax.swing.UIManager

enum class SubscriptionType {

    Pro {
        override fun getTabnineLogo(): Icon {
            return genIcon()
        }
    };

    abstract fun getTabnineLogo(): Icon
    fun genIcon(): Icon {
        val instance = AiReviewSettingsState.getInstance()
        val color = UIManager.getColor("TextField.foreground")
        val DELAY = 30
        val ICONS: List<Icon> = ContainerUtil.immutableList(
                IconUtil.colorize(IconLoader.findIcon("/icons/ai_jetbrains_completion_icon_loading1.svg")!!, color),
                IconUtil.colorize(IconLoader.findIcon("/icons/ai_jetbrains_completion_icon_loading1.svg")!!, color),
                IconUtil.colorize(IconLoader.findIcon("/icons/ai_jetbrains_completion_icon_loading1.svg")!!, color),
                IconUtil.colorize(IconLoader.findIcon("/icons/ai_jetbrains_completion_icon_loading1.svg")!!, color),
                IconUtil.colorize(IconLoader.findIcon("/icons/ai_jetbrains_completion_icon_loading1.svg")!!, color),
                IconUtil.colorize(IconLoader.findIcon("/icons/ai_jetbrains_completion_icon_loading1.svg")!!, color),
                IconUtil.colorize(IconLoader.findIcon("/icons/ai_jetbrains_completion_icon_loading1.svg")!!, color),
                IconUtil.colorize(IconLoader.findIcon("/icons/ai_jetbrains_completion_icon_loading1.svg")!!, color)
        )
        return if (instance.reviewIng) {
            AnimatedIcon(DELAY, *ICONS.toTypedArray())
        } else {
            IconUtil.colorize(IconLoader.findIcon("/icons/ai_jetbrains_completion_icon_loading1.svg")!!, color)
        }
    }
}

fun getSubscriptionType(): SubscriptionType {
    return SubscriptionType.Pro
}
