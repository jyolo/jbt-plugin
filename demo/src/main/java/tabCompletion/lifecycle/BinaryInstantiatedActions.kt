package tabCompletion.lifecycle

import tabCompletion.binary.requests.config.ConfigRequest
import tabCompletion.binary.BinaryRequestFacade

class BinaryInstantiatedActions(private val binaryRequestFacade: BinaryRequestFacade) {
    fun openHub() {
        binaryRequestFacade.executeRequest(ConfigRequest())
    }
}
