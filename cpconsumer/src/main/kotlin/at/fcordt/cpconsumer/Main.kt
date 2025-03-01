package at.fcordt.cpconsumer

import at.fcordt.cpconsumer.models.AuthHookResponse
import at.fcordt.cpconsumer.models.AuthRequest
import at.fcordt.cpconsumer.models.AuthResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.apache.kafka.clients.consumer.KafkaConsumer
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

val client = HttpClient(CIO) {
    install(HttpTimeout) {
        requestTimeoutMillis = (System.getenv("REQUEST_TIMEOUT")?.toLongOrNull() ?: 1000L)
    }
    install(ContentNegotiation) {
        json()
    }
}


suspend fun main() {
    //since it's sutch a small service, don't bother with services, etc...
    val authServerUrl = System.getenv("AUTH_SERVER_URL") ?: "localhost:8084"
    KafkaConsumer<String, AuthRequest>(mapOf(
        "bootstrap.servers" to (System.getenv("KAFKA_BOOTSTRAP_SERVERS") ?: "localhost:9092"),
        "auto.offset.reset" to "earliest",
        "key.deserializer" to "org.apache.kafka.common.serialization.StringDeserializer",
        "value.deserializer" to "at.fcordt.cpconsumer.models.AuthRequestDeserializer",
        "group.id" to "consumer_group_1",
        "security.protocol" to "PLAINTEXT"
    )).use {
        it.subscribe(listOf(System.getenv("KAFKA_TOPIC") ?: "cpauth"))
        while(true) {
            val message = it.poll(100.milliseconds.toJavaDuration())
            if(message != null && !message.isEmpty) {
                for(record in message) {
                    handleAuthRequest(record.value(), "$authServerUrl/api/v1/internal/auth")
                }
            }
        }
    }
}

suspend fun handleAuthRequest(value: AuthRequest, requestUrl: String) {
    //call Auth Server
    if(value.callbackUrl != null) {
        val resp = client.get(requestUrl)
        val authResponse : AuthResponse = resp.body()
        client.post(value.callbackUrl) {
            contentType(ContentType.Application.Json)
            when (resp.status) {
                HttpStatusCode.OK -> setBody(
                    AuthHookResponse(
                        value.stationId,
                        value.driverId,
                        authResponse.status?.toAuthHookStatus()
                    )
                )

                HttpStatusCode.RequestTimeout -> setBody(
                    AuthHookResponse(
                        value.stationId,
                        value.driverId,
                        AuthHookResponse.Status.unknown
                    )
                )

                else -> setBody(
                    AuthHookResponse(
                        value.stationId,
                        value.driverId,
                        AuthHookResponse.Status.invalid
                    )
                )
            }
        }
    }
}
