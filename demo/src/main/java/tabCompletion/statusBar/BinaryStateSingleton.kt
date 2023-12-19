package tabCompletion.lifecycle

import com.intellij.util.messages.Topic
import tabCompletion.binary.requests.config.StateResponse
import tabCompletion.general.TopicBasedState
import java.util.function.Consumer

class BinaryStateSingleton private constructor() :
    TopicBasedState<StateResponse, BinaryStateSingleton.OnChange>(
        TOPIC
    ) {
    companion object {
        private val TOPIC = Topic.create("Binary State Changed Notifier", OnChange::class.java)

        @JvmStatic
        val instance = BinaryStateSingleton()
    }

    public fun interface OnChange : Consumer<StateResponse>
}
