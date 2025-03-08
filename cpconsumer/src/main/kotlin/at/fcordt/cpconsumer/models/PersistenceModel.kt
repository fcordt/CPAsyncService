package at.fcordt.cpconsumer.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import java.util.*

@Serializable
data class PersistenceModel(
    @SerialName("_id")
    @Contextual
    val id: ObjectId,
    @SerialName("request_time")
    val requestTime: Instant,
    @SerialName("station_id")
    @Serializable(with = UUIDSerializer::class)
    val stationId: UUID?,
    @SerialName("driver_id")
    val driverId: String?,
    val status: AuthHookResponse.Status?)
