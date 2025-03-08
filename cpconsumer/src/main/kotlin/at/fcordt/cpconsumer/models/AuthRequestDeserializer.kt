package at.fcordt.cpconsumer.models

import kotlinx.serialization.json.Json
import org.apache.kafka.common.serialization.Deserializer

@Suppress("unused")
class AuthRequestDeserializer : Deserializer<AuthRequest> {
    override fun deserialize(topic: String?, data: ByteArray?): AuthRequest {
        val dataString = data?.decodeToString() ?: ""
        return try {
            Json.decodeFromString(dataString) as AuthRequest
        } catch (ex: Exception) {
            NULL_AUTH_REQUEST
        }
    }
}