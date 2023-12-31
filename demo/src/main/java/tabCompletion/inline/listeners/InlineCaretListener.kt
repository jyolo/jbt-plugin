package tabCompletion.inline.listeners

import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.util.Disposer
import tabCompletion.binary.requests.notifications.shown.SuggestionDroppedReason
import tabCompletion.general.DependencyContainer
import tabCompletion.inline.CompletionPreview
import tabCompletion.inline.InlineCompletionCache

class InlineCaretListener(private val completionPreview: CompletionPreview) : CaretListener, Disposable {
    private val completionsEventSender = DependencyContainer.instanceOfCompletionsEventSender()
    init {
        Disposer.register(completionPreview, this)
        completionPreview.editor.caretModel.addCaretListener(this)
    }

    override fun caretPositionChanged(event: CaretEvent) {
        if (isSingleOffsetChange(event)) {
            return
        }

        completionsEventSender.sendSuggestionDropped(
            completionPreview.editor, completionPreview.currentCompletion, SuggestionDroppedReason.CaretMoved
        )

        Disposer.dispose(completionPreview)
        InlineCompletionCache.instance.clear(event.editor)
    }

    private fun isSingleOffsetChange(event: CaretEvent): Boolean {
        return event.oldPosition.line == event.newPosition.line && event.oldPosition.column + 1 == event.newPosition.column
    }

    override fun dispose() {
        completionPreview.editor.caretModel.removeCaretListener(this)
    }
}
