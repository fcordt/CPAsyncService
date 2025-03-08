package at.fcordt.cpbackend.models


import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val status: Status? = null,
)
{
    enum class Status(val value: String){
        Accepted("accepted"),
        Denied("denied");
    }
}

