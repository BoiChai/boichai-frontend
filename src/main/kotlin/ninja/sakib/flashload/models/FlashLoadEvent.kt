package ninja.sakib.flashload.models

import ninja.sakib.pultusorm.annotations.AutoIncrement
import ninja.sakib.pultusorm.annotations.PrimaryKey
import java.util.*

class FlashLoadEvent {
    @AutoIncrement
    @PrimaryKey
    var id: Int = 0
    var event: MqttEvent = MqttEvent.CONNECTED
    var clientID: String = ""
    var topic: String = ""
    var payload: String = ""
    var createdAt: Date = Calendar.getInstance().time
}
