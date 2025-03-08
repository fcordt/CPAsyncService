package at.fcordt.cpconsumer.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class AuthHookResponse(
    @Serializable(with = UUIDSerializer::class)
    @SerialName("station_id")
    val stationId: UUID? = null,
    @SerialName("driver_id")
    val driverId: String? = null,
    /* This URL will be called when the authorization process is completed. No URL checking as per requirements. */
    @SerialName("callback_url")
    val status: Status? = null
) {
    enum class Status(val value: String){
        Allowed("allowed"),
        NotAllowed("not_allowed"),
        Unknown("unknown"),
        Invalid("invalid");
    }
}