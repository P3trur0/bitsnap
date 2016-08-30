/**
 *  Copyright 2016 Yurly Yarosh
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

import bitsnap.exceptions.AlreadyAssignedException
import java.net.URL
import java.util.*

/**
 * HTTP 1.1 Request
 */
class Request internal constructor(
    val method: Method,
    val url: URL,
    val body: Body,
    val headers: List<Header>,
    val version: String = "1.1") {

    fun methodUrlToString() = "$method $url"

    override fun toString() =
        "${methodUrlToString()} HTTP/$version\r\n${headers.joinToString { "$it\r\n" }}\r\n$body"

    class Builder internal constructor(private val headers: MutableList<Header> = LinkedList(),
                                       private var body: Body? = null) {

        @Throws(AlreadyAssignedException::class)
        fun header(header: Header) {
            if (!header.allowMultiple && headers.filter { it.name == header.name }.size > 0) {
                throw AlreadyAssignedException(header.name)
            }

            synchronized(headers) {
                headers.add(header)
            }
        }

        @Throws(AlreadyAssignedException::class)
        fun headers(vararg headers: Header) {
            headers.forEach { header(it) }
        }

        @Throws(AlreadyAssignedException::class)
        fun body(body: Body) {
            synchronized(body) {
                if (this.body == null) {
                    this.body = body
                } else {
                    throw AlreadyAssignedException("body")
                }
            }
        }

        operator fun invoke(method: Method, url: URL, init: Builder.() -> Unit): Request {
            init()
            return Request(method, url, body ?: Body.Empty, headers)
        }
    }
}
