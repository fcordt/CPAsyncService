package at.fcordt.cpauth.models

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.kafka.common.serialization.Serializer

class AuthRequestSerializer : Serializer<AuthRequest> {
    override fun serialize(topic: String?, data: AuthRequest?): ByteArray {
        return Json.encodeToString(data).toByteArray()
    }
}