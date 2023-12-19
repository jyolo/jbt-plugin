package tabCompletion.binary.requests.notifications

import tabCompletion.binary.BinaryRequest

class NotificationsBinaryRequest : BinaryRequest<NotificationsBinaryResponse> {
    override fun response(): Class<NotificationsBinaryResponse> {
        return NotificationsBinaryResponse::class.java
    }

    override fun serialize(): Any {
        return mapOf("Notifications" to emptyMap<Any, Any>())
    }
}
