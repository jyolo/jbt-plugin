package tabCompletion.inline

import tabCompletion.prediction.TabNineCompletion

interface OnCompletionPreviewUpdatedCallback {
    fun onCompletionPreviewUpdated(completion: TabNineCompletion)
}
