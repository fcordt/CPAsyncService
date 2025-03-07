package at.fcordt.cpconsumer.services

import at.fcordt.cpconsumer.models.AuthHookResponse
import at.fcordt.cpconsumer.models.AuthRequest
import at.fcordt.cpconsumer.models.PersistenceModel
import com.mongodb.kotlin.client.coroutine.MongoClient
import org.bson.types.ObjectId
import kotlinx.datetime.Clock

interface LoginPersistService {
    suspend fun persistLogin(request: AuthRequest, response: AuthHookResponse)
}

class LoginPersistServiceImpl(mongoConnectionString : String, databaseName: String, private val mongoCollectionName: String) : LoginPersistService {
    private val client = MongoClient.create(mongoConnectionString)
    private val db = client.getDatabase(databaseName)

    override suspend fun persistLogin(request: AuthRequest, response: AuthHookResponse) {
        val collection = db.getCollection<PersistenceModel>(mongoCollectionName)
        collection.insertOne(PersistenceModel(ObjectId(), Clock.System.now(), request.stationId, request.driverId, response.status))
    }
}