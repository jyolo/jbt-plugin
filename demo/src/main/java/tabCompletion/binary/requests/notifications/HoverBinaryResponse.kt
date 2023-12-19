package tabCompletion.binary.requests.notifications

import com.google.gson.annotations.SerializedName
import tabCompletion.general.NotificationOption
import tabCompletion.binary.BinaryResponse

data class HoverBinaryResponse(
    val id: String,
    val message: String,
    val title: String?,
    val options: Array<NotificationOption>,
    @SerializedName("notification_type")
    val notificationType: String?,
    val state: Any?,
) : BinaryResponse
