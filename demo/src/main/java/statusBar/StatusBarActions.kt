package statusBar

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.wm.StatusBar
import settings.AiReviewSettingsState

const val OPEN_TABNINE_HUB_TEXT = "Open Ai Review"
// const val GETTING_STARTED_TEXT = "Getting Started Guide"

object StatusBarActions {

    @JvmStatic
    fun buildStatusBarActionsGroup(
            myStatusBar: StatusBar?,
            id: String
    ): DefaultActionGroup {
        val actions = ArrayList<AnAction>()
        actions.add(enablePluginAction(myStatusBar, id))
        return DefaultActionGroup(actions)
    }

    private fun enablePluginAction(myStatusBar: StatusBar?, id: String): DumbAwareAction {
        val instance = AiReviewSettingsState.getInstance()
        val message = if (instance.enableAiReview) {
            "Disable (Current Enabled)"
        } else {
            "Enable (Current Disabled)"
        }
        return DumbAwareAction.create(
                message
        ) {
            instance.enableAiReview = !instance.enableAiReview
            if (myStatusBar != null) {
                myStatusBar.updateWidget(id)
            }
        }
    }
}
