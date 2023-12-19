package tabCompletion.lifecycle

import com.intellij.util.messages.Topic
import tabCompletion.capabilities.Capabilities
import tabCompletion.general.TopicBasedState
import java.util.function.Consumer

class CapabilitiesStateSingleton private constructor() :
    TopicBasedState<Capabilities, CapabilitiesStateSingleton.OnChange>(
        TOPIC
    ) {
    companion object {
        private val TOPIC = Topic.create("tabCompletion.capabilties", OnChange::class.java)

        @JvmStatic
        val instance = CapabilitiesStateSingleton()
    }

    public fun interface OnChange : Consumer<Capabilities>
}
