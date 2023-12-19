package tabCompletion.binary.requests.analytics

import tabCompletion.binary.BinaryRequest
import tabCompletion.binary.requests.EmptyResponse

data class EventRequest(var name: String, var properties: Map<String, String>?) :
    BinaryRequest<EmptyResponse> {
    override fun response(): Class<EmptyResponse> {
        return EmptyResponse::class.java
    }

    override fun serialize(): Any {
        return mapOf("Event" to this)
    }
}
