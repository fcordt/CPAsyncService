package at.fcordt.cpbackend.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

/**
 * 
 * @param stationId 
 * @param driverId
 */
@Serializable
data class AuthRequest(
    @Serializable(with = UUIDSerializer::class)
    @SerialName("station_id")
    val stationId: UUID? = null,
    @SerialName("driver_id")
    val driverId: String? = null,
)

