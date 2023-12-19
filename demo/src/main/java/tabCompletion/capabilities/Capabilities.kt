package tabCompletion.capabilities

import tabCompletion.binary.requests.capabilities.ExperimentSource

data class Capabilities(
    val features: Set<Capability>,
    val experimentSource: ExperimentSource?
) {
    fun isReady() = experimentSource == null || experimentSource.isRemoteBasedSource()

    fun isEnabled(capability: Capability) = features.contains(capability)

    fun anyEnabled(vararg capabilities: Capability) =
        features.intersect(setOf(*capabilities)).isNotEmpty()
}
