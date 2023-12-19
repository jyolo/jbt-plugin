package tabCompletion.state

import tabCompletion.binary.requests.config.StateRequest
import tabCompletion.general.DependencyContainer
import tabCompletion.general.ServiceLevel

class UserState private constructor() {
    private val suggestionHintState: SuggestionHintState = TODO()
    val serviceLevel: ServiceLevel?

    init {
        val binaryRequestFacade = DependencyContainer.instanceOfBinaryRequestFacade()
//        val stateResponse = binaryRequestFacade.executeRequest(StateRequest())
//        suggestionHintState = SuggestionHintState(stateResponse?.installationTime)
//        serviceLevel = stateResponse?.serviceLevel
    }

    companion object {
        var userState: UserState? = null

        @JvmStatic
        fun init() {
            if (userState == null) {
                userState = UserState()
            }
        }

        @JvmStatic
        val instance: UserState
            get() {
                init()
                return userState!!
            }
    }
}
