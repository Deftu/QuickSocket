package xyz.deftu.quicksocket.server

import xyz.deftu.quicksocket.client.QuickSocketClient
import java.net.InetSocketAddress
import java.net.URI

/**
 * Creates a basic non-custom [QuickSocketClient].
 */
fun client(block: QuickSocketClientBlock.() -> Unit) = QuickSocketClientBlock().apply(block).build()
/**
 * Creates a basic non-custom [QuickSocketServer].
 */
fun server(block: QuickSocketServerBlock.() -> Unit) = QuickSocketServerBlock().apply(block).build()

/**
 * Used for the QuickSocket Kotlin DSL.
 */
class QuickSocketClientBlock {
    lateinit var uri: URI
    lateinit var url: String

    var encoded = true
    var headers = mutableMapOf<String, String>()

    var reuseAddr = false
    var tcpNoDelay = false

    fun headers(block: SocketHeadersBlock.() -> Unit) = SocketHeadersBlock().apply(block).also { headers = it.build() }
    fun build(): QuickSocketClient {
        val value = if (this::uri.isInitialized) QuickSocketClient(uri, encoded, headers)
        else if (this::url.isInitialized) QuickSocketClient(url, encoded, headers)
        else throw IllegalArgumentException("Please provide a URI or URL for your socket.")
        value.isReuseAddr = reuseAddr
        value.isTcpNoDelay = tcpNoDelay
        return value
    }
}

/**
 * Used for the QuickSocket Kotlin DSL.
 */
class QuickSocketServerBlock {
    lateinit var address: InetSocketAddress
    lateinit var hostname: String
    var port: Int? = null

    var encoded = true

    var reuseAddr = false
    var tcpNoDelay = false

    fun build(): QuickSocketServer {
        val value = if (this::address.isInitialized) QuickSocketServer(address, encoded)
        else if (this::hostname.isInitialized && port != null) QuickSocketServer(InetSocketAddress(hostname, port!!), encoded)
        else if (this::hostname.isInitialized) QuickSocketServer(InetSocketAddress(hostname, 8080), encoded)
        else QuickSocketServer(encoded = encoded)
        value.isReuseAddr = reuseAddr
        value.isTcpNoDelay = tcpNoDelay
        return value
    }
}

/**
 * Used for the QuickSocket Kotlin DSL.
 */
class SocketHeadersBlock {
    private val value = mutableMapOf<String, String>()
    fun add(pair: Pair<String, String>) = this.value.put(pair.first, pair.second)
    fun add(name: String, value: String) = add(name to value)
    fun remove(name: String) = value.remove(name)
    fun build() = value
}