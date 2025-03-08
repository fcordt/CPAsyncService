package at.fcordt.cpconsumer.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class BackendAuthRequest(
    @Serializable(with = UUIDSerializer::class)
    @SerialName("station_id")
    val stationId: UUID? = null,
    @SerialName("driver_id")
    val driverId: String? = null,
)