package at.fcordt.cpconsumer.services

import at.fcordt.cpconsumer.models.AuthRequest
import at.fcordt.cpconsumer.models.AuthResponse

interface LoginPersistService {
    fun persisLogin(request: AuthRequest, response: AuthResponse)
}

class LoginPersistServiceImpl : LoginPersistService {
    override fun persisLogin(request: AuthRequest, response: AuthResponse) {
        TODO("Not yet implemented")
    }
}