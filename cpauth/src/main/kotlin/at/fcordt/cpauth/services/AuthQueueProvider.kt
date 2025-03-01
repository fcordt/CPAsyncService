package at.fcordt.cpauth.services

import at.fcordt.cpauth.models.AuthRequest
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.concurrent.TimeUnit


interface AuthQueueProvider {
    fun insertAuthRequest(authRequest: AuthRequest)
}

class AuthQueueProviderImpl(bootstrapServer : String, private val topic: String, private val timeOutInMilliSeconds : Long) : AuthQueueProvider {
    private val producer = KafkaProducer<String, AuthRequest>(mapOf(
        "bootstrap.servers" to bootstrapServer,
        "key.serializer" to "org.apache.kafka.common.serialization.StringSerializer",
        "value.serializer" to "at.fcordt.cpauth.models.AuthRequestSerializer",
        "security.protocol" to "PLAINTEXT"
    ))

    override fun insertAuthRequest(authRequest: AuthRequest) {
        producer.use {
            it.send(ProducerRecord(topic, authRequest)).get(timeOutInMilliSeconds, TimeUnit.MILLISECONDS)
        }
    }
}