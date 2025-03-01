package at.fcordt.cpbackend.models


import kotlinx.serialization.Serializable
/**
 * 
 * @param status
 */
@Serializable
data class AuthResponse(
    val status: Status? = null,
)
{
    /**
    * 
    * Values: accepted,denied
    */
    enum class Status(val value: String){
        accepted("accepted"),
        denied("denied");
    }
}

