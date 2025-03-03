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
val persistService : LoginPersistService = LoginPersistServiceImpl(
    (System.getenv("MONGO_DB_CONNECTION_STRING") ?: "mongodb://root:password@mongo:27017/"),
    (System.getenv("MONGO_DB_NAME") ?: "PersistenceDB"),
    (System.getenv("MONGO_COLLECTION_NAME") ?: "LoginCollection"))


fun main() {
    val authServerUrl = System.getenv("AUTH_SERVER_URL") ?: "localhost:8084"
    queueConsumer.consume {
        runBlocking {
            launch {
                val resp = handleAuthRequest(it, "$authServerUrl/api/v1/internal/auth")
                persistService.persistLogin(it, resp)
            }
        }
    }
}

suspend fun handleAuthRequest(value: AuthRequest, requestUrl: String) : AuthHookResponse {
    val resp = client.get(requestUrl)
    val authResponse : AuthResponse = resp.body()
    val status = when (resp.status) {
        HttpStatusCode.OK -> authResponse.status?.toAuthHookStatus() ?: AuthHookResponse.Status.invalid
        HttpStatusCode.RequestTimeout -> AuthHookResponse.Status.unknown
        else -> AuthHookResponse.Status.invalid
    }
    val hookResponse = AuthHookResponse(value.stationId, value.driverId, status)
    if(value.callbackUrl != null) {
        client.post(value.callbackUrl) {
            setBody(hookResponse)
        }
    }
    return hookResponse
}
