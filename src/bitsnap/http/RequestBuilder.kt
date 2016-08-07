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

package bitsnap.http

import com.fasterxml.jackson.databind.JsonNode
import java.util.*

class RequestBuilder internal constructor(val uri: String, val method: Method) {

    internal var headers: MutableList<Header> = LinkedList()

    internal var body: Body = Body.EmptyBody

    open class RequestBuilderException(massage: String) : HttpBuildException(massage)

    fun header(header: Header) {
        synchronized(headers) {
            if (!header.allowMultiple && headers.filter { it.name == header.name }.size > 0) {
                throw HeaderAlreadyAssignedException(header.name)
            }

            headers.add(header)
        }
    }

    class HeaderAlreadyAssignedException(name: String) : RequestBuilderException("Request header $name have been assigned already")

    private fun body(body: Body) {
        synchronized(body) {
            if (this.body == Body.EmptyBody) {
                this.body = body
            } else {
                throw BodyAlreadyAssignedException()
            }
        }
    }

    fun json(node: JsonNode) {
        body(Body.JsonBody(node))
    }

    fun body(bodyString: String) {
        body(Body.StringBody(bodyString))
    }

    class BodyAlreadyAssignedException : RequestBuilderException("Request body have been assigned already")

    internal fun build() = Request(uri, method, body, headers)
}
