package at.fcordt.cpconsumer

import at.fcordt.cpconsumer.models.AuthHookResponse
import at.fcordt.cpconsumer.models.AuthRequest
import at.fcordt.cpconsumer.models.AuthResponse
import at.fcordt.cpconsumer.services.AuthQueueConsumerImpl
import at.fcordt.cpconsumer.services.LoginPersistService
import at.fcordt.cpconsumer.services.LoginPersistServiceImpl
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

val client = HttpClient(CIO) {
    install(HttpTimeout) {
        requestTimeoutMillis = (System.getenv("REQUEST_TIMEOUT")?.toLongOrNull() ?: 1000L)
    }
    install(ContentNegotiation) {
        json()
    }
}

val queueConsumer = AuthQueueConsumerImpl((System.getenv("KAFKA_BOOTSTRAP_SERVERS") ?: "localhost:9092"),
    System.getenv("KAFKA_TOPIC") ?: "cpauth")
val persistService : LoginPersistService = LoginPersistServiceImpl()


fun main() {
    val authServerUrl = System.getenv("AUTH_SERVER_URL") ?: "localhost:8084"
    queueConsumer.consume {
        runBlocking {
            launch {
                val resp = handleAuthRequest(it, "$authServerUrl/api/v1/internal/auth")
                persistService.persisLogin(it, resp)
            }
        }
    }
}

suspend fun handleAuthRequest(value: AuthRequest, requestUrl: String) : AuthResponse {
    //call Auth Server
    val resp = client.get(requestUrl)
    val authResponse : AuthResponse = resp.body()
    if(value.callbackUrl != null) {
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
    return authResponse
}
