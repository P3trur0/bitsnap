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

import bitsnap.http.Body
import bitsnap.http.Header
import bitsnap.http.Response
import bitsnap.http.Status
import com.fasterxml.jackson.databind.JsonNode
import bitsnap.http.Request as HttpRequest
import java.util.*

class Application internal constructor(
    private val server: Server = Server.default(Application.requestHandler),
    val client: Client
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

    companion object {
        internal val requestHandler = { request: HttpRequest ->
            Response(Status.Success.OK, Body(""), emptyList())
        }
    }
}

