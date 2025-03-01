package at.fcordt.cpauth.apis

import at.fcordt.cpauth.Paths
import at.fcordt.cpauth.models.AuthRequest
import at.fcordt.cpauth.models.AuthResponse
import at.fcordt.cpauth.services.AuthQueueProvider
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get
import java.util.concurrent.TimeoutException

fun Route.DefaultApi() {
    //oh boy, see https://github.com/InsertKoinIO/koin/issues/2008
    val authQueueProvider = application.get<AuthQueueProvider>()

    post<Paths.chargingAuthRequestPost> {
        val authRequest = call.receive<AuthRequest>()
        try {
            authQueueProvider.insertAuthRequest(authRequest)
            call.respond(HttpStatusCode.OK, AuthResponse(
                AuthResponse.Status.accepted,
                "Request is being processed asynchronously. The result will be sent to the provided callback URL."
            ))
        } catch (e: TimeoutException) {
            call.respond(HttpStatusCode.RequestTimeout, AuthResponse(
                AuthResponse.Status.denied,
                "Timeout"
            ))
        }

    }

}
