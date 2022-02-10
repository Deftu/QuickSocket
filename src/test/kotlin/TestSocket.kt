import xyz.deftu.quicksocket.server.client
import xyz.deftu.quicksocket.server.server

fun main() {
    val socketServer = server {
        tcpNoDelay = true
    }.start()

    val socketClient = client {
        url = "ws://localhost:443/"
        headers {
            add("User-Agent" to "Test-Agent (1.0)")
        }
    }.connect()
}