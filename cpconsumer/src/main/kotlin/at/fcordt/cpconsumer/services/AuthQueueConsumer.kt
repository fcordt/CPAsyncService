package at.fcordt.cpconsumer.services

import at.fcordt.cpconsumer.models.AuthRequest
import io.ktor.util.logging.*
import io.ktor.utils.io.core.*
import org.apache.kafka.clients.consumer.KafkaConsumer
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

interface AuthQueueConsumer : Closeable {
    fun consume(block : (AuthRequest) ->  (Unit))
}

val logger = KtorSimpleLogger("AuthQueueConsumerImpl")

class AuthQueueConsumerImpl(kafkaBootstrapServers: String, kafkaTopic: String) : AuthQueueConsumer {
    private val kafkaConsumer = KafkaConsumer<String, AuthRequest>(mapOf(
    "bootstrap.servers" to kafkaBootstrapServers,
    "auto.offset.reset" to "earliest",
    "key.deserializer" to "org.apache.kafka.common.serialization.StringDeserializer",
    "value.deserializer" to "at.fcordt.cpconsumer.models.AuthRequestDeserializer",
    "group.id" to "consumer_group_1",
    "security.protocol" to "PLAINTEXT"
    ))

    init {
        kafkaConsumer.subscribe(listOf(kafkaTopic))
    }

    override fun consume(block : (AuthRequest) ->  (Unit)) {
            while(true) {
                val message = kafkaConsumer.poll(100.milliseconds.toJavaDuration())
                if(message != null && !message.isEmpty) {
                    try {
                        for (record in message) {
                            block.invoke(record.value())
                        }
                    } catch(e: Throwable) {
                        //logging and rethrowing - we don't commit the kafka queue if there's no connection to the backend
                        logger.error("Error consuming auth request", e)
                        throw e
                    }
                }
                kafkaConsumer.commitSync()
            }
    }

    override fun close() {
        kafkaConsumer.close()
    }
}