package at.fcordt.cpauth.services

import at.fcordt.cpauth.models.AuthRequest

interface AuthQueueProvider {
    fun insertAuthRequest(authRequest: AuthRequest)
}

class AuthQueueProviderImpl : AuthQueueProvider {

    override fun insertAuthRequest(authRequest: AuthRequest) {
        //todo: insert into Kafka
    }
}