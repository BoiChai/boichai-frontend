package ninja.sakib.flashload.consumer

import com.eclipsesource.json.Json
import de.jupf.staticlog.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ninja.sakib.flashload.core.FlashLoadTracer
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.lang.Exception
import kotlin.properties.Delegates

class FlashLoadClient(var clientId: String, var username: String, var password: String, var tracer: FlashLoadTracer, var mqttUrl: String) : MqttCallback {
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

        client = MqttClient(mqttUrl, clientId, MemoryPersistence())
        client.setCallback(this)

        return GlobalScope.launch {
            try {
                client.connect(options)
                Log.info("[Client = $clientId] connection request has been sent successfully.")
            } catch (e: Exception) {
                Log.info("[Client = $clientId] failed to connect [error = ${e.message}].")
            }
        }
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        try {
            if (message != null) {
                Log.info("[Client = $clientId] received new message [topic = $topic] - [message = ${String(message.payload)}].")

                val pld = Json.parse(String(message.payload)).asObject()
                val action = pld.get("action").asString()

                when (action) {
                    "client_connected" -> {
                        tracer.onClientConnected(clientId)
                    }
                    "message_published" -> {
                        tracer.onMessageReceived(this.clientId, topic!!, String(message.payload))
                    }
                }
            }
        } catch (e: Exception) {
            Log.error("Message received with error ${e.message}")
        }
    }

    override fun connectionLost(cause: Throwable?) {
        Log.info("[Client = $clientId] disconnected. [cause = ${cause!!.message}]")
        tracer.onClientDisconnected(this.clientId)
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
    }

    fun disconnect() {
        GlobalScope.launch {
            try {
                if (client.isConnected) {
                    Log.info("[Client = $clientId] Disconnecting")
                    client.disconnect()
                    tracer.onClientDisconnected(clientId)
                }
            } catch (e: Exception) {
                Log.info("[Client = $clientId] Disconnecting failed. [error = ${e.message}]")
            }
        }
    }

    fun isConnected(): Boolean {
        return client.isConnected
    }
}
