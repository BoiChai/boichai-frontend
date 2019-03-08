package ninja.sakib.flashload.consumer

import de.jupf.staticlog.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ninja.sakib.flashload.core.FlashLoadTracer
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.lang.Exception
import kotlin.properties.Delegates

class FlashLoadClient(var clientId: String, var username: String, var password: String, var tracer: FlashLoadTracer) : MqttCallback {
    private var client: MqttClient by Delegates.notNull()

    fun connect(): Job {
        val options = MqttConnectOptions()
        options.userName = username
        options.password = password.toCharArray()
        options.connectionTimeout = 60
        options.isAutomaticReconnect = false
        options.isCleanSession = false
        options.keepAliveInterval = 60
        options.isAutomaticReconnect = true

        client = MqttClient("tcp://35.154.124.33:1883", clientId, MemoryPersistence())
        client.setCallback(this)

        return GlobalScope.launch {
            try {
                client.connect(options)
                Log.info("[Client = $clientId] has been connected.")
                tracer.onClientConnected(clientId)
            } catch (e: Exception) {
                Log.info("[Client = $clientId] failed to connect [error = ${e.message}].")
            }
        }
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        Log.info("[Client = $clientId] received new message [topic = $topic].")
        tracer.onMessageReceived(this.clientId, topic!!, String(message!!.payload))
    }

    override fun connectionLost(cause: Throwable?) {
        Log.info("[Client = $clientId] disconnected. [cause = ${cause!!.message}]")
        tracer.onClientDisconnected(this.clientId)
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
    }

    fun isConnected(): Boolean {
        return client.isConnected
    }
}
