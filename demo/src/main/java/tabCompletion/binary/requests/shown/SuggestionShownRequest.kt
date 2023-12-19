package tabCompletion.binary.requests.notifications.shown

import tabCompletion.binary.BinaryRequest
import tabCompletion.binary.exceptions.TabNineInvalidResponseException
import tabCompletion.binary.requests.EmptyResponse
import tabCompletion.binary.requests.autocomplete.CompletionMetadata

data class SuggestionShownRequest(
    var net_length: Int,
    var filename: String,
    var metadata: CompletionMetadata
) : BinaryRequest<EmptyResponse> {
    override fun response(): Class<EmptyResponse> {
        return EmptyResponse::class.java
    }

    override fun serialize(): Any {
        return mapOf("SuggestionShown" to this)
    }

    override fun shouldBeAllowed(e: TabNineInvalidResponseException): Boolean {
        return true
    }
}
