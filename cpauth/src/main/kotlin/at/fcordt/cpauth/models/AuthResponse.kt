package at.fcordt.cpauth.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val status: Status? = null,
    val message: String? = null
)
{
    enum class Status(val value: String){
        Accepted("accepted"),
        Denied("denied");
    }
}

