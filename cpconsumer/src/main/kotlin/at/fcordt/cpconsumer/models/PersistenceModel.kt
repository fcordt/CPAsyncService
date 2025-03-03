package at.fcordt.cpconsumer.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import kotlinx.datetime.Instant
import java.util.UUID

data class PersistenceModel(
    @BsonId
    val id: ObjectId,
    @BsonProperty("resquest_time")
    val requestTime: Instant,
    @BsonProperty("station_id")
    val stationId: UUID?,
    @BsonProperty("driver_id")
    val driverId: String?,
    val status: AuthHookResponse.Status?)
