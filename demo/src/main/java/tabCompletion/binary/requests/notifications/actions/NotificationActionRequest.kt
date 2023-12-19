package tabCompletion.binary.requests.notifications.actions

import com.google.gson.annotations.SerializedName
import tabCompletion.binary.BinaryRequest
import tabCompletion.binary.requests.EmptyResponse

data class NotificationActionRequest(
    var id: String?,
    var selected: String?,
    var message: String?,
    @SerializedName("notification_type") var notificationType: String?,
    var actions: List<Any>?,
    var state: Any?,
) : BinaryRequest<EmptyResponse> {
    override fun response(): Class<EmptyResponse> {
        return EmptyResponse::class.java
    }

    override fun serialize(): Any {
        return mapOf("NotificationAction" to this)
    }
}
