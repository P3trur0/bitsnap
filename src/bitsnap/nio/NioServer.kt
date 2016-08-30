package bitsnap.nio

import bitsnap.Router
import bitsnap.Server
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.channels.*
import java.util.concurrent.ConcurrentHashMap

class NioServer(router: Router) : Server(router) {

    val selector : Selector
    var serverChannel : ServerSocketChannel
    private var listening : Boolean = false

    override fun isListening() = listening

    init {
        try {
            selector = Selector.open()
            serverChannel = ServerSocketChannel.open()
        } catch (e: IOException) {
            throw e
        }
    }

    override fun stop() {
        listening = false
    }

    private val readBuffer = ByteBuffer.allocateDirect(1024)
    private val writeBuffer = ByteBuffer.allocateDirect(1024)

    val pendingData : MutableMap<Socket, ByteArray> = ConcurrentHashMap()

    fun listen(host: String, port: Int, dataHandler: (InetAddress, ByteArray, (ByteArray) -> Unit, () -> Unit) -> Unit) {
        serverChannel.bind(InetSocketAddress(host, port))
        serverChannel.configureBlocking(false)

        val ops = serverChannel.validOps()
        serverChannel.register(selector, ops, null)

        listening = true
        while (listening) {
            selector.select()
            val iterator = selector.selectedKeys()

            iterator.removeAll(iterator.map { key ->
                if (key.isAcceptable) {
                    val newClientChannel = serverChannel.accept()
                    newClientChannel.register(selector, SelectionKey.OP_READ)
                } else if (key.isReadable) {
                    val clientChannel = key.channel() as SocketChannel

                    try {
                        serverChannel.bind(InetSocketAddress(host, port))
                        serverChannel.configureBlocking(false)

                        val ops = serverChannel.validOps()
                        serverChannel.register(selector, ops, null)

                        listening = true
                        while (listening) {
                            selector.select()
                            val iterator = selector.selectedKeys()

                            iterator.removeAll(iterator.map { key ->

                                when {
                                    !key.isValid -> key

                                    key.isAcceptable -> {
                                        val newClientChannel = serverChannel.accept()
                                        newClientChannel.register(selector, SelectionKey.OP_READ.or(SelectionKey.OP_WRITE))
                                    }

                                    key.isReadable -> {
                                        val clientChannel = key.channel() as SocketChannel
                                        try {
                                            clientChannel.read(readBuffer)

                                            val writeData = fun (data: ByteArray) { pendingData.put(clientChannel.socket(), data) }

                                            val close = fun() { clientChannel.close() }

                                            dataHandler(clientChannel.socket().inetAddress, ByteArray(readBuffer.capacity()) {
                                                readBuffer[it]
                                            }, writeData, close)

                                        } catch (e: IOException) {}
                                    }

                                    key.isWritable -> {
                                        val clientChannel = key.channel() as SocketChannel
                                        try {
                                            val pendingBuffer = pendingData[clientChannel.socket()]
                                            if (pendingBuffer?.isNotEmpty() ?: false) {
                                                writeBuffer.put(pendingBuffer)
                                                clientChannel.write(writeBuffer)
                                            }
                                        } catch (e: IOException) {

                                        }
                                    }

                                    else -> key
                                }

                                key
                            })
                        }
                    } catch (e: IOException) {

                    }
                }

                key
            })
        }
    }

    override fun start(host: String, port: Int, then: () -> Unit) {
    }

    override fun startSSL(host: String, port: Int, then: () -> Unit) {
    }
}
