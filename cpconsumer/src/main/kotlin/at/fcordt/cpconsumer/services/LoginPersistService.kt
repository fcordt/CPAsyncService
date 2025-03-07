package at.fcordt.cpconsumer.services

import at.fcordt.cpconsumer.models.AuthHookResponse
import at.fcordt.cpconsumer.models.AuthRequest
import at.fcordt.cpconsumer.models.PersistenceModel
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.bson.types.ObjectId
import kotlinx.datetime.Clock
import kotlin.reflect.KProperty

interface LoginPersistService {
    suspend fun persistLogin(request: AuthRequest, response: AuthHookResponse)
}

class LoginPersistServiceImpl(val mongoConnectionString : String, val databaseName: String, private val mongoCollectionName: String) : LoginPersistService {
    private val db by MongoClient

    override suspend fun persistLogin(request: AuthRequest, response: AuthHookResponse) {
        val collection = db.getCollection<PersistenceModel>(mongoCollectionName)
        collection.insertOne(PersistenceModel(ObjectId(), Clock.System.now(), request.stationId, request.driverId, response.status))
    }
}

private operator fun MongoClient.Factory.getValue(
    loginPersistServiceImpl: LoginPersistServiceImpl,
    property: KProperty<*>
): MongoDatabase {
    return create(loginPersistServiceImpl.mongoConnectionString).getDatabase(loginPersistServiceImpl.databaseName)
}

