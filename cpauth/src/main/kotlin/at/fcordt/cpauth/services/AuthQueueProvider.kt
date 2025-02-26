package at.fcordt.cpauth.services

import at.fcordt.cpauth.models.AuthRequest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.Serializer
import java.util.concurrent.TimeUnit


class AuthRequestSerializer : Serializer<AuthRequest> {
    override fun serialize(topic: String?, data: AuthRequest?): ByteArray {
        return Json.encodeToString(data).toByteArray()
    }
}

interface AuthQueueProvider {
    fun insertAuthRequest(authRequest: AuthRequest)
}

class AuthQueueProviderImpl(bootstrapServer : String, private val topic: String, private val timeOutInMilliSeconds : Long) : AuthQueueProvider {
    private val settings = mapOf(
        "bootstrap.servers" to bootstrapServer,
        "key.serializer" to "org.apache.kafka.common.serialization.StringSerializer",
        "value.serializer" to "at.fcordt.cpauth.services.AuthRequestSerializer",
        "security.protocol" to "PLAINTEXT"
    )

    override fun insertAuthRequest(authRequest: AuthRequest) {
        val producer = KafkaProducer<String, AuthRequest>(settings)
        producer.use {
            it.send(ProducerRecord(topic, authRequest)).get(timeOutInMilliSeconds, TimeUnit.MILLISECONDS)
        }
    }
}