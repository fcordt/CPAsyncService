package at.fcordt.cpconsumer.models


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
    enum class Status(val value: String, private val authHookStatus: AuthHookResponse.Status){
        accepted("accepted", AuthHookResponse.Status.allowed),
        denied("denied", AuthHookResponse.Status.notAllowed);

        fun toAuthHookStatus() = authHookStatus
    }
}

