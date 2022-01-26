package xyz.deftu.quicksocket.common.packets

import com.google.gson.JsonObject
import xyz.deftu.quicksocket.common.utils.*

abstract class PacketBase @JvmOverloads constructor(
    private val identifier: String
) {
    abstract fun onPacketSent(data: JsonObject)
    abstract fun onPacketReceived(data: JsonObject?)

    fun asJson() = buildJsonObject {
        addProperty("identifier", identifier)
    }
}