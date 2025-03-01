package at.fcordt.cpbackend.services

import at.fcordt.cpbackend.models.AuthRequest
import java.util.UUID

interface AccessControlList {
    operator fun contains(request: AuthRequest): Boolean
}

class InMemoryWhiteList(users : Collection<String>, stations : Collection<UUID>) : AccessControlList {
    private val allowedDrivers : Set<String> = users.toSet()
    private val allowedStations : Set<UUID> = stations.toSet()

    override operator fun contains(request: AuthRequest) : Boolean {
        return request.driverId in allowedDrivers && request.stationId in allowedStations
    }

}