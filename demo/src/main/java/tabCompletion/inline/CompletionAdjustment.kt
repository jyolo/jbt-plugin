package tabCompletion.inline

import tabCompletion.vo.AutocompleteRequest
import tabCompletion.vo.AutocompleteResponse
import tabCompletion.general.SuggestionTrigger

abstract class CompletionAdjustment {
    abstract val suggestionTrigger: SuggestionTrigger
    var cachedOnly: Boolean = false

    fun withCachedOnly(): CompletionAdjustment {
        cachedOnly = true
        return this
    }

    fun adjustRequest(request: AutocompleteRequest): AutocompleteRequest {
//        request.cached_only = cachedOnly
        return adjustRequestInner(request)
    }

    protected open fun adjustRequestInner(autocompleteRequest: AutocompleteRequest): AutocompleteRequest = autocompleteRequest
    open fun adjustResponse(autocompleteResponse: AutocompleteResponse): AutocompleteResponse = autocompleteResponse
}
