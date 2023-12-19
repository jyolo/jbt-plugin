package tabCompletion.inline

import tabCompletion.general.SuggestionTrigger

class DefaultCompletionAdjustment : CompletionAdjustment() {

    override val suggestionTrigger: SuggestionTrigger
        get() = SuggestionTrigger.DocumentChanged
}
