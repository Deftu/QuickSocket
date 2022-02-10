package xyz.deftu.quicksocket.common.packets

import com.google.gson.JsonObject
import xyz.deftu.quicksocket.common.utils.*

abstract class PacketBase(
    private val identifier: String
) {
    abstract fun onPacketSent(data: JsonObject)
    abstract fun onPacketReceived(data: JsonObject?)

    /**
     * Creates a [JsonObject] version of this packet.
     *
     * The packet data is not available via this method,
     * it is provided to/by the socket implementation.
     */
    fun asJson() = buildJsonObject {
        addProperty("identifier", identifier)
    }
}