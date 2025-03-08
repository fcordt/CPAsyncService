package at.fcordt.cpbackend.apis

import at.fcordt.cpbackend.Paths
import at.fcordt.cpbackend.models.AuthRequest
import at.fcordt.cpbackend.models.AuthResponse
import at.fcordt.cpbackend.services.AccessControlList
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Route.defaultApi() {
    //oh boy, see https://github.com/InsertKoinIO/koin/issues/2008
    val accessControlList = application.get<AccessControlList>()

    post<Paths.ChargingAuthRequestPost> {
        val authRequest = call.receive<AuthRequest>()
        if(authRequest in accessControlList) {
            call.respond(HttpStatusCode.OK, AuthResponse(AuthResponse.Status.Accepted))
        } else {
            call.respond(HttpStatusCode.OK, AuthResponse(AuthResponse.Status.Denied))
        }

    }

}
