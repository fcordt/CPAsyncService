package at.fcordt.cpconsumer.models

import kotlinx.serialization.json.Json
import org.apache.kafka.common.serialization.Deserializer

class AuthRequestDeserializer : Deserializer<AuthRequest> {
    override fun deserialize(topic: String?, data: ByteArray?): AuthRequest {
        val dataString = data?.decodeToString() ?: ""
        try {
            return Json.decodeFromString(dataString) as AuthRequest
        } catch (ex: Exception) {
            return NULL_AUTH_REQUEST
        }
    }
}