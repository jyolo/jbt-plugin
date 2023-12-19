package tabCompletion.binary.requests.notifications

import tabCompletion.binary.BinaryResponse

data class NotificationsBinaryResponse(var notifications: List<BinaryNotification>? = null) : BinaryResponse
