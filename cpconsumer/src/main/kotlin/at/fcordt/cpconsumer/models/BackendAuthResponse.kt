package at.fcordt.cpconsumer.models


import kotlinx.serialization.Serializable

@Serializable
data class BackendAuthResponse(
    val status: Status? = null,
)
{
    @Suppress("unused")
    enum class Status(val value: String, private val authHookStatus: AuthHookResponse.Status){
        Accepted("accepted", AuthHookResponse.Status.Allowed),
        Denied("denied", AuthHookResponse.Status.NotAllowed);

        fun toAuthHookStatus() = authHookStatus
    }
}

