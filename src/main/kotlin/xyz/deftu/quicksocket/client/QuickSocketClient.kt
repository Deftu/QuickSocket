package xyz.deftu.quicksocket.client

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.stream.MalformedJsonException
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import xyz.deftu.quicksocket.common.CloseCode
import xyz.deftu.quicksocket.common.QuickSocketConstants
import xyz.deftu.quicksocket.common.exceptions.KeyAlreadyBoundException
import xyz.deftu.quicksocket.common.packets.PacketBase
import xyz.deftu.quicksocket.common.utils.*
import java.net.URI
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import kotlin.Exception

open class QuickSocketClient @JvmOverloads constructor(
    uri: URI,
    val encoded: Boolean = true,
    headers: Map<String, String> = mapOf()
) : WebSocketClient(
    uri,
    headers
) {
    private val packets: MutableMap<String, Class<out PacketBase>> by lazy {
        mutableMapOf()
    }

    @JvmOverloads constructor(uri: String, encoded: Boolean = true, headers: Map<String, String> = mapOf()) : this(URI.create(uri), encoded, headers)

    /* Implementation. */

    final override fun onOpen(handshake: ServerHandshake) {
        onConnectionOpened(handshake)
    }

    final override fun onClose(code: Int, reason: String, remote: Boolean) {
        onConnectionClosed(CloseCode.from(code), reason, remote)
    }

    final override fun onError(ex: Exception) {
        onErrorOccurred(ex)
    }

    final override fun onMessage(bytes: ByteBuffer) {
        val message = StandardCharsets.UTF_8.decode(bytes).toString()
        if (encoded) {
            handleMessage(message)
        } else onMessage(message)
        onMessageReceived(bytes)
    }

    final override fun onMessage(message: String) {
        if (!encoded) {
            handleMessage(message)
        } else onMessage(ByteBuffer.wrap(message.toByteArray()))
        onMessageReceived(message)
    }

    private fun handleMessage(message: String) {
        if (!message.isJson()) return
        val raw = JsonParser.parseString(message)
        if (!raw.isJsonObject) throw MalformedJsonException("Expected JsonObject, received ${raw::class.java.simpleName}")
        val parsed = raw.asJsonObject
        if (!parsed.has("identifier")) return
        val identifier = parsed.get("identifier").asString
        if (!packets.containsKey(identifier)) return
        val clz = packets[identifier]!!
        val constructor = try {
            clz.getConstructor()
        } catch (e: Exception) {
            null
        } ?: return
        val packet = constructor.newInstance()
        packet.onPacketReceived(parsed.getAsJsonObject("data") ?: null)
    }

    /**
     * Adds a new packet to the valid packet registry.
     */
    fun addPacket(identifier: String, packet: Class<out PacketBase>) {
        if (packets.containsKey(identifier)) throw KeyAlreadyBoundException()
        packets[identifier] = packet
    }

    /**
     * Sends a packet to the server that this client is currently connected to.
     */
    fun sendPacket(packet: PacketBase) {
        val data = JsonObject()
        packet.onPacketSent(data)

        val json = packet.asJson()
        json.add("data", data)

        val content = QuickSocketConstants.GSON.toJson(json)
        if (encoded) send(StandardCharsets.UTF_8.encode(content))
        else send(content)
    }

    /* Overrides. */

    open fun onConnectionOpened(handshake: ServerHandshake) {
    }

    open fun onConnectionClosed(code: CloseCode, reason: String, remote: Boolean) {
    }

    open fun onErrorOccurred(throwable: Throwable) {
    }

    open fun onMessageReceived(bytes: ByteBuffer) {
    }

    open fun onMessageReceived(message: String) {
    }

}