package xyz.deftu.quicksocket.server

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.stream.MalformedJsonException
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import xyz.deftu.quicksocket.common.CloseCode
import xyz.deftu.quicksocket.common.QuickSocketConstants
import xyz.deftu.quicksocket.common.exceptions.KeyAlreadyBoundException
import xyz.deftu.quicksocket.common.packets.PacketBase
import xyz.deftu.quicksocket.common.utils.isJson
import java.lang.Exception
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

open class QuickSocketServer @JvmOverloads constructor(
    address: InetSocketAddress? = InetSocketAddress(8080),
    val encoded: Boolean = true
) : WebSocketServer(
    address
) {
    private val packets: MutableMap<String, Class<out PacketBase>> by lazy {
        mutableMapOf()
    }

    /* Implementation. */

    final override fun onStart() {
        onSocketStarted()
    }

    final override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        onConnectionOpened(conn, handshake)
    }

    final override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
        onConnectionClosed(conn, CloseCode.from(code), reason, remote)
    }

    final override fun onError(conn: WebSocket, ex: Exception) {
        onErrorOccurred(conn, ex)
    }

    final override fun onMessage(conn: WebSocket, bytes: ByteBuffer) {
        val message = StandardCharsets.UTF_8.decode(bytes).toString()
        if (encoded) {
            handleMessage(message)
        } else onMessage(conn ,message)
        onMessageReceived(conn, bytes)
    }

    final override fun onMessage(conn: WebSocket, message: String) {
        if (!encoded) {
            handleMessage(message)
        } else onMessage(conn, ByteBuffer.wrap(message.toByteArray()))
        onMessageReceived(conn, message)
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
     * Sends a packet to one of the clients that this server has a connection to.
     */
    fun sendPacket(connection: WebSocket, packet: PacketBase) {
        val data = JsonObject()
        packet.onPacketSent(data)

        val json = packet.asJson()
        json.add("data", data)

        val content = QuickSocketConstants.GSON.toJson(json)
        if (encoded) connection.send(StandardCharsets.UTF_8.encode(content))
        else connection.send(content)
    }

    /* Overrides. */

    open fun onSocketStarted() {
    }

    open fun onConnectionOpened(connection: WebSocket, handshake: ClientHandshake) {
    }

    open fun onConnectionClosed(connection: WebSocket, code: CloseCode, reason: String, remote: Boolean) {
    }

    open fun onMessageReceived(connection: WebSocket, bytes: ByteBuffer) {
    }

    open fun onMessageReceived(connection: WebSocket, message: String) {
    }

    open fun onErrorOccurred(connection: WebSocket, throwable: Throwable) {
    }

}