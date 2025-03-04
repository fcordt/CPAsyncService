package at.fcordt.cpauth.models


import kotlinx.serialization.Serializable
/**
 * 
 * @param status 
 * @param message 
 */
@Serializable
data class AuthResponse(
    val status: Status? = null,
    val message: String? = null
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

