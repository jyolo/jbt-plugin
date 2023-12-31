package tabCompletion.binary.requests.statusBar

import tabCompletion.binary.BinaryRequest
import tabCompletion.binary.exceptions.TabNineInvalidResponseException

class StatusBarPromotionBinaryRequest :
    BinaryRequest<StatusBarPromotionBinaryResponse> {
    override fun response(): Class<StatusBarPromotionBinaryResponse> {
        return StatusBarPromotionBinaryResponse::class.java
    }

    override fun serialize(): Any {
        return mapOf("StatusBar" to emptyMap<Any, Any>())
    }

    override fun shouldBeAllowed(e: TabNineInvalidResponseException): Boolean {
        // If there is no promotion to show, the binary returns "null" as response, so we just ignore it.
        return e.rawResponse.filter { it == "null" }.isPresent
    }
}
