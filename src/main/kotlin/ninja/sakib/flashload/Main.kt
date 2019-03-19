package ninja.sakib.flashload

import com.eclipsesource.json.Json
import de.jupf.staticlog.Log
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ninja.sakib.flashload.consumer.FlashLoadClient
import ninja.sakib.flashload.report.SqliteTracer
import java.io.File
import java.io.FileReader

fun main(args: Array<String>) = runBlocking {
    val db = SqliteTracer

    if (args.isEmpty()) {
        Log.warn("Config file required (pass as first parameter)")
        System.exit(0)
    }

    val f = File(args[0])
    val config = Json.parse(FileReader(f)).asObject()

    val mqttURL = config.get("mqtt_uri").asString()
    val maxConnect = config.get("max_connect").asInt()
    val batchInterval = config.get("batch_interval").asInt()
    val batchLimit = config.get("batch_limit").asInt()
    var batchCount = 0
    var connectedCount = 0

    Log.info("URL : $mqttURL")
    Log.info("Target : $maxConnect")
    Log.info("Interval : $batchInterval")
    Log.info("BatchLimit : $batchLimit")


    val connectedClients: MutableList<FlashLoadClient> = mutableListOf()

    val clients = config.get("clients").asArray()

    Log.info("Clients configured : ${clients.size()}")
    Log.info("===========================")
    Log.info("===========================")

    for (c in clients) {
        val cfg = c.asObject()
        val clientId = cfg.get("client_id").asString()
        val username = cfg.get("username").asString()
        val password = cfg.get("password").asString()

        Log.info("[Client_id = $clientId] connecting...")

        Log.info("Username = $username")
        Log.info("Password = $password")

        val client = FlashLoadClient(clientId, username, password, db, mqttURL)
        launch {
            client.connect()
        }
        connectedClients.add(client)

        connectedCount++
        batchCount++

        if (connectedCount >= maxConnect) {
            Log.info("Connection Requested : $connectedCount, Actual : ${connectedClients.size}")
            break
        }
        if (batchCount == batchLimit) {
            Log.info("Batch reached : $batchLimit, with interval : $batchInterval s")

            try {
                Thread.sleep(1000.toLong() * batchInterval)
            } catch (e: Exception) {
                Log.info("Thread interrupted [msg=${e.message}]")
            } finally {
                batchCount = 0
            }
        }
    }

    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run() {
            doOnAppClosing(connectedClients)
        }
    })

    joinAll()
}

private fun doOnAppClosing(clients: MutableList<FlashLoadClient>) = runBlocking {
    Log.info("Detected shutdown event. Closing connections...")
    for (c in clients) {
        launch {
            c.disconnect()
        }
    }
}

private fun isClientsConnected(clients: MutableList<FlashLoadClient>): Boolean {
    for (c in clients) {
        if (c.isConnected()) {
            return true
        }
    }
    return false
}
