package ninja.sakib.flashload

import com.eclipsesource.json.Json
import de.jupf.staticlog.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ninja.sakib.flashload.consumer.FlashLoadClient
import ninja.sakib.flashload.report.SqliteTracer
import java.io.File
import java.io.FileReader
import java.lang.Exception

suspend fun main(args: Array<String>) {
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

    val connectedClients: MutableList<FlashLoadClient> = mutableListOf()
    val runningJobs: MutableList<Job> = mutableListOf()

    val clients = config.get("clients").asArray()
    for (c in clients) {
        val cfg = c.asObject()
        val clientId = cfg.get("client_id").asString()
        val username = cfg.get("username").asString()
        val password = cfg.get("password").asString()

        Log.info("[Client_id = $clientId] connecting...")

        Log.info("Username = $username")
        Log.info("Password = $password")

        val client = FlashLoadClient(clientId, username, password, db, mqttURL)
        runningJobs.add(client.connect())
        connectedClients.add(client)

        connectedCount++
        batchCount++

        if (connectedCount >= maxConnect) {
            break
        }
        if (batchCount == batchLimit) {
            try {
                Thread.sleep(1000.toLong() * batchInterval)
            } catch (e: Exception) {

            }
        }
    }

    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run() {
            doOnAppClosing(connectedClients)
        }
    })

    GlobalScope.launch {
        for (j in runningJobs) {
            j.join()
        }
    }.join()

    while (isClientsConnected(clients = connectedClients)) {
        Thread.sleep(1000 * 5)
    }
}

private fun doOnAppClosing(clients: MutableList<FlashLoadClient>) {
    Log.info("Detected shutdown event. Closing connections...")
    for (c in clients) {
        c.disconnect()
    }
    Thread.sleep(5 * 1000)
}

private fun isClientsConnected(clients: MutableList<FlashLoadClient>): Boolean {
    for (c in clients) {
        if (c.isConnected()) {
            return true
        }
    }
    return false
}
