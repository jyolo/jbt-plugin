package tabCompletion.binary.requests.notifications.shown

import tabCompletion.binary.BinaryRequest
import tabCompletion.binary.exceptions.TabNineInvalidResponseException
import tabCompletion.binary.requests.EmptyResponse
import tabCompletion.binary.requests.autocomplete.CompletionMetadata

enum class SuggestionDroppedReason {
    ManualCancel,
    ScrollLookAhead,
    TextDeletion,
    UserNotTypedAsSuggested,
    CaretMoved,
    FocusChanged,
}

data class SuggestionDroppedRequest(
    var net_length: Int,
    var reason: SuggestionDroppedReason? = null,
    var filename: String? = null,
    var metadata: CompletionMetadata? = null
) : BinaryRequest<EmptyResponse> {
    override fun response(): Class<EmptyResponse> {
        return EmptyResponse::class.java
    }

    override fun serialize(): Any {
        return mapOf("SuggestionDropped" to this)
    }

    override fun shouldBeAllowed(e: TabNineInvalidResponseException): Boolean {
        return true
    }
}
