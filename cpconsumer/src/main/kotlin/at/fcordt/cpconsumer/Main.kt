package at.fcordt.cpconsumer

import at.fcordt.cpconsumer.models.AuthHookResponse
import at.fcordt.cpconsumer.models.AuthRequest
import at.fcordt.cpconsumer.models.BackendAuthResponse
import at.fcordt.cpconsumer.services.AuthQueueConsumerImpl
import at.fcordt.cpconsumer.services.LoginPersistService
import at.fcordt.cpconsumer.services.LoginPersistServiceImpl
import at.fcordt.cpconsumer.services.ModelTransformers.toAuthHookResponse
import at.fcordt.cpconsumer.services.ModelTransformers.toBackendAuthRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.logging.*
import kotlinx.coroutines.runBlocking

val client = HttpClient(CIO) {
    install(HttpTimeout) {
        requestTimeoutMillis = (System.getenv("REQUEST_TIMEOUT")?.toLongOrNull() ?: 1000L)
    }
    install(ContentNegotiation) {
        json()
    }
}

val queueConsumer = AuthQueueConsumerImpl((System.getenv("KAFKA_BOOTSTRAP_SERVER") ?: "localhost:9092"),
    System.getenv("KAFKA_TOPIC") ?: "cpauth")
val persistService : LoginPersistService = LoginPersistServiceImpl(
    (System.getenv("MONGO_DB_CONNECTION_STRING") ?: "mongodb://root:password@mongo:27017/"),
    (System.getenv("MONGO_DB_NAME") ?: "PersistenceDB"),
    (System.getenv("MONGO_COLLECTION_NAME") ?: "LoginCollection"))


@Suppress("HttpUrlsUsage")
fun main() {
    var authServerUrl = (System.getenv("AUTH_SERVER_URL") ?: "localhost:8084") + "/api/v1/internal/auth"
    if(!authServerUrl.startsWith("http://") && !authServerUrl.startsWith("https://")) {
        authServerUrl = "http://$authServerUrl"
    }

    queueConsumer.consume {
        runBlocking {
            val resp = handleAuthRequest(it, authServerUrl)
            persistService.persistLogin(it, resp)
        }
    }
}

val logger = KtorSimpleLogger("QueueConsumer")

suspend fun handleAuthRequest(value: AuthRequest, requestUrl: String) : AuthHookResponse {
    val resp = client.post(requestUrl) {
        setBody(value.toBackendAuthRequest())
        contentType(ContentType.Application.Json)
    }
    logger.info("got response $resp")
    val status = when (resp.status) {
        HttpStatusCode.OK -> resp.body<BackendAuthResponse>().status?.toAuthHookStatus() ?: AuthHookResponse.Status.Invalid
        HttpStatusCode.RequestTimeout -> AuthHookResponse.Status.Unknown
        else -> AuthHookResponse.Status.Invalid
    }
    val hookResponse = value.toAuthHookResponse(status)
    if(value.callbackUrl != null) {
        try {
            client.post(value.callbackUrl) {
                setBody(hookResponse)
                contentType(ContentType.Application.Json) //what plugin am I missing to automatically set this?
            }
        } catch (e: Exception) { //what else to do?
            logger.warn("Got exception $e while trying to call ${value.callbackUrl}", e)
        }
    }
    return hookResponse
}
