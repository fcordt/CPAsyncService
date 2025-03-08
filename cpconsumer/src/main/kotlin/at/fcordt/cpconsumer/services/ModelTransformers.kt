package at.fcordt.cpconsumer.services

import at.fcordt.cpconsumer.models.AuthHookResponse
import at.fcordt.cpconsumer.models.AuthRequest
import at.fcordt.cpconsumer.models.BackendAuthRequest

object ModelTransformers {
    fun AuthRequest.toBackendAuthRequest() : BackendAuthRequest {
        return BackendAuthRequest(this.stationId, this.driverId)
    }

    fun AuthRequest.toAuthHookResponse(status: AuthHookResponse.Status) : AuthHookResponse {
        return AuthHookResponse(this.stationId, this.driverId, status)
    }
}