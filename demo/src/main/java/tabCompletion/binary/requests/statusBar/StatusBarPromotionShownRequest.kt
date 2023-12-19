package tabCompletion.binary.requests.statusBar

import com.google.gson.annotations.SerializedName
import tabCompletion.binary.BinaryRequest
import tabCompletion.binary.requests.selection.SetStateBinaryResponse
import tabCompletion.general.StaticConfig

class StatusBarPromotionShownRequest(
    var id: String?,
    private val text: String,
    @SerializedName("notification_type")
    val notificationType: String?,
    val state: Any?,
) : BinaryRequest<SetStateBinaryResponse> {
    override fun response(): Class<SetStateBinaryResponse> {
        return SetStateBinaryResponse::class.java
    }

    override fun serialize(): Any {
        return mapOf("SetState" to mapOf("state_type" to mapOf("StatusShown" to this)))
    }

    override fun validate(response: SetStateBinaryResponse): Boolean {
        return StaticConfig.SET_STATE_RESPONSE_RESULT_STRING == response.result
    }
}
