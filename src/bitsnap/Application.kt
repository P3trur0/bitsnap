/**
 *  Copyright 2016 Yuriy Yarosh
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 **/

package bitsnap

import bitsnap.http.Header
import bitsnap.nio.NioServer
import com.fasterxml.jackson.databind.JsonNode
import bitsnap.http.Request as HttpRequest
import java.util.*

class Application internal constructor(
    private val config: Application.Config,
    private val router : Router = Router(),
    private val server: Server = NioServer(router),
    private val client: Client
    ) {

    class Request(
        val request: HttpRequest,
        val headers: MutableList<Header> = LinkedList()
    ) {

        fun header(header: Header) {

        }

        fun json(node: JsonNode) {

        }

        fun json(string: String) {

        }

        fun json(any: Any) {

        }

        companion object {
            internal operator fun invoke(httpRequest: HttpRequest) = Request(httpRequest)
        }
    }

    data class Config (
        val host: String,
        val port: Int,
        val isSSL: Boolean
    ) {
        data class Builder(
            private var host: String? = null,
            private var port: Int? = null,
            private var isSSL: Boolean? = null
        ) {
            fun host(host: String) { this.host = host }
            fun port(port: Int) { this.port = port }
            fun ssl() { this.isSSL = true }

            companion object {
                operator fun invoke(init: Builder.() -> Unit) : Config{
                    val builder = Builder()
                    builder.init()
                    return Config(builder.host ?: "0.0.0.0",
                        builder.port ?: 8080,
                        builder.isSSL ?: false)
                }
            }
        }
    }

    companion object {
        operator fun invoke(configInit: Application.Config.Builder.() -> Unit) {}
    }
}

