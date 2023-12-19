package tabCompletion.binary.requests.capabilities

import com.google.gson.annotations.SerializedName
import tabCompletion.binary.BinaryResponse
import tabCompletion.capabilities.Capability

data class CapabilitiesResponse(
    @SerializedName("enabled_features")
    var enabledFeatures: List<Capability>? = null,
    @SerializedName("experiment_source")
    var experimentSource: ExperimentSource? = null
) : BinaryResponse

enum class ExperimentSource {
    @SerializedName("API")
    API,

    @SerializedName("APIErrorResponse")
    APIErrorResponse,

    @SerializedName("Hardcoded")
    Hardcoded,

    @SerializedName("Unknown")
    Unknown;

    fun isRemoteBasedSource() = this == API || this == APIErrorResponse
}
