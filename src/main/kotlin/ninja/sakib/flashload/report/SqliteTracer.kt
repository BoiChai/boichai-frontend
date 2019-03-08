package ninja.sakib.flashload.report

import de.jupf.staticlog.Log
import ninja.sakib.flashload.core.FlashLoadTracer
import ninja.sakib.flashload.models.FlashLoadEvent
import ninja.sakib.flashload.models.MqttEvent
import ninja.sakib.pultusorm.core.PultusORM
import java.lang.Exception

object SqliteTracer : FlashLoadTracer {
    private var connection: PultusORM? = null

    init {
        synchronized(this) {
            connection = PultusORM("flash_load.db")
        }
    }

    override fun onClientConnected(clientID: String): Boolean {
        return try {
            val e = FlashLoadEvent()
            e.clientID = clientID
            e.event = MqttEvent.CONNECTED
            connection!!.save(e)
        } catch (e: Exception) {
            Log.error(e.message!!)
            false
        }
    }

    override fun onClientDisconnected(clientID: String): Boolean {
        return try {
            val e = FlashLoadEvent()
            e.clientID = clientID
            e.event = MqttEvent.DISCONNECTED
            connection!!.save(e)
        } catch (e: Exception) {
            Log.error(e.message!!)
            false
        }
    }

    override fun onMessageSend(clientID: String, topic: String, msg: String): Boolean {
        return try {
            val e = FlashLoadEvent()
            e.clientID = clientID
            e.event = MqttEvent.MESSAGE_SEND
            e.topic = topic
            e.payload = msg
            connection!!.save(e)
        } catch (e: Exception) {
            Log.error(e.message!!)
            false
        }
    }

    override fun onMessageReceived(clientID: String, topic: String, msg: String): Boolean {
        return try {
            val e = FlashLoadEvent()
            e.clientID = clientID
            e.event = MqttEvent.MESSAGE_RECEIVED
            e.topic = topic
            e.payload = msg
            connection!!.save(e)
        } catch (e: Exception) {
            Log.error(e.message!!)
            false
        }
    }
}
