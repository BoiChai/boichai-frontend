package ninja.sakib.flashload.core

interface FlashLoadTracer {
    fun onMessageReceived(clientID: String, topic: String, msg: String): Boolean
    fun onClientConnected(clientID: String): Boolean
    fun onClientDisconnected(clientID: String): Boolean
    fun onMessageSend(clientID: String, topic: String, msg: String): Boolean
}
