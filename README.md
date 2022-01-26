<div align="center">

# `QuickSocket`
Quick and easy WebSocket implementation.\
[Report a bug][bugreps]
·
[Request a feature][featreq]

</div>

## Examples
### Java
<details>
    <summary>Packets</summary>

```java
import com.google.gson.JsonObject;
import xyz.deftu.quicksocket.common.packets.PacketBase;

public class PacketHelloWorld extends PacketBase {
    public PacketHelloWorld() {
        super("HELLO_WORLD");
    }

    public void onPacketSent(JsonObject data) {
        data.addProperty("Hello!", "How are you?");
    }

    public void onPacketReceived(JsonObject data) {
        System.out.println("I just received a greeting packet! Wow!");
    }
}
```
</details>

<details>
    <summary>Client</summary>

```java
import org.java_websocket.handshake.ServerHandshake;
import packets.PacketHelloWorld;
import xyz.deftu.quicksocket.client.QuickSocketClient;

import java.net.URI;

public class ExampleSocketClient extends QuickSocketClient {
    public ExampleSocketClient() {
        super(URI.create("ws://localhost:4567"));
        addPacket("HELLO_WORLD", PacketHelloWorld.class);
    }

    public void onConnectionOpened(ServerHandshake handshake) {
        System.out.println("Connection opened!");
    }

    public static void main(String[] args) {
        try {
            new ExampleSocketClient().connectBlocking();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```
</details>

<details>
    <summary>Server</summary>

```java
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import packets.PacketHelloWorld;
import xyz.deftu.quicksocket.server.QuickSocketServer;

import java.net.InetSocketAddress;

public class ExampleSocketServer extends QuickSocketServer {
    public ExampleSocketServer() {
        super(new InetSocketAddress(4567));
    }

    public void onSocketStarted() {
        addPacket("HELLO_WORLD", PacketHelloWorld.class);
    }

    public void onConnectionOpened(WebSocket connection, ClientHandshake handshake) {
        System.out.println("Connection opened!");
        sendPacket(connection, new PacketHelloWorld());
    }

    public static void main(String[] args) {
        new ExampleSocketServer().start();
    }
}
```
</details>

### Kotlin
<details>
    <summary>Packets</summary>

```kt
import xyz.deftu.quicksocket.common.packets.PacketBase
import com.google.gson.JsonObject

class PacketHelloWorld : PacketBase(
    "HELLO_WORLD"
) {
    override fun onPacketSent(data: JsonObject) {
        data.addProperty("Hello!", "How are you?")
    }

    override fun onPacketReceived(data: JsonObject?) {
        println("I just received a greeting packet! Wow!")
    }
}
```
</details>

<details>
    <summary>Client</summary>

```kt
import xyz.deftu.quicksocket.client.QuickSocketClient
import org.java_websocket.handshake.ServerHandshake
import packets.PacketHelloWorld
import java.net.URI

class ExampleSocketClient : QuickSocketClient(
    URI.create("ws://localhost:4567")
) {
    override fun onConnectionOpened(handshake: ServerHandshake) {
        println("Connection opened!")
    }

    init {
        addPacket("HELLO_WORLD", PacketHelloWorld::class.java)
    }
}
```
</details>

<details>
    <summary>Server</summary>

```kt
import packets.PacketHelloWorld
import xyz.deftu.quicksocket.server.QuickSocketServer
import java.net.InetSocketAddress
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake

class ExampleSocketServer : QuickSocketServer(
    InetSocketAddress(4567)
) {
    override fun onSocketStarted() {
        addPacket("HELLO_WORLD", PacketHelloWorld::class.java)
    }

    override fun onConnectionOpened(connection: WebSocket, handshake: ClientHandshake) {
        println("Connection opened!")
        sendPacket(connection, PacketHelloWorld())
    }
}
```
</details>

[bugreps]: https://github.com/Deftu/QuickSocket/issues
[featreq]: https://github.com/Deftu/QuickSocket/issues