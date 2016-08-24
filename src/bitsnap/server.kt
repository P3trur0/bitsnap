package bitsnap

import bitsnap.http.Request
import bitsnap.http.Response

abstract class Server internal constructor(val handler: (request: Request) -> Response) {

    abstract fun listen(host: String, port: Int, then: () -> Unit)

    fun listen(port: Int, then: () -> Unit) = listen("", port, then)

    fun listen(then: () -> Unit) = listen("", 80, then)

    abstract fun listenSSL(host: String, port: Int, then: () -> Unit)

    fun listenSSL(port: Int, then: () -> Unit) = listen("", 443, then)

    fun listenSSL(then: () -> Unit) = listen(443, then)

    companion object {
        fun default(handler: (request: Request) -> Response) = NioServer(handler)
    }
}

class NioServer(handler: (request: Request) -> Response) : Server(handler) {

    override fun listen(host: String, port: Int, then: () -> Unit) {
        then()
    }

    override fun listenSSL(host: String, port: Int, then: () -> Unit) {
        then()
    }
}
