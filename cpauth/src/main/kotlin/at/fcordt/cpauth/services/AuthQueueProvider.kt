package at.fcordt.cpauth.services

import at.fcordt.cpauth.models.AuthRequest
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import java.io.Closeable

interface AuthQueueProvider {
    fun insertAuthRequest(authRequest: AuthRequest)
}

class AuthQueueProviderImpl(bootstrapServer : String, private val topic: String) : AuthQueueProvider, Closeable {
    private val producer = KafkaProducer<String, AuthRequest>(mapOf(
        "bootstrap.servers" to bootstrapServer,
        "key.serializer" to "org.apache.kafka.common.serialization.StringSerializer",
        "value.serializer" to "org.apache.kafka.common.serialization.ByteArraySerializer",
        "security.protocol" to "PLAINTEXT"
    ))

    override fun insertAuthRequest(authRequest: AuthRequest) {
        producer.send(ProducerRecord(topic, authRequest))
    }

    override fun close() {
        producer.close()
    }
}