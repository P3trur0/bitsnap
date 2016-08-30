package bitsnap

import bitsnap.nio.NioServer

abstract class Server internal constructor(val router: Router) {

    abstract fun start(host: String, port: Int, then: () -> Unit)

    abstract fun startSSL(host: String, port: Int, then: () -> Unit)

    abstract fun isListening() : Boolean

    abstract fun stop()

    companion object {
        fun default(router: Router) = NioServer(router)
    }
}
