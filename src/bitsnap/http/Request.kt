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

/**
 * HTTP 1.1 Request
 */
class Request internal constructor(
    val uri: String,
    val method: Method,
    val body: Body? = null,
    val headers: List<Header>,
    val version: String = "1.1") {

    override fun toString() =
        "$method $uri HTTP/$version\r\n${headers.joinToString { "$it\r\n" }}\r\n${body?.contents() ?: ""}"

    companion object {
        fun from(requestString: String) {
            print(requestString)
        }

        fun Options(uri: String, init: RequestBuilder.() -> Unit): Request {
            val builder = RequestBuilder(uri, Method.OPTIONS)
            builder.init()
            return builder.build()
        }

        fun Get(uri: String, init: RequestBuilder.() -> Unit): Request {
            val builder = RequestBuilder(uri, Method.GET)
            builder.init()
            return builder.build()
        }

        fun Head(uri: String, init: RequestBuilder.() -> Unit): Request {
            val builder = RequestBuilder(uri, Method.HEAD)
            builder.init()
            return builder.build()
        }

        fun Put(uri: String, init: RequestBuilder.() -> Unit): Request {
            val builder = RequestBuilder(uri, Method.PUT)
            builder.init()
            return builder.build()
        }

        fun Post(uri: String, init: RequestBuilder.() -> Unit): Request {
            val builder = RequestBuilder(uri, Method.POST)
            builder.init()
            return builder.build()
        }

        fun Delete(uri: String, init: RequestBuilder.() -> Unit): Request {
            val builder = RequestBuilder(uri, Method.DELETE)
            builder.init()
            return builder.build()
        }

        fun Trace(uri: String, init: RequestBuilder.() -> Unit): Request {
            val builder = RequestBuilder(uri, Method.TRACE)
            builder.init()
            return builder.build()
        }

        fun Connect(uri: String, init: RequestBuilder.() -> Unit): Request {
            val builder = RequestBuilder(uri, Method.CONNECT)
            builder.init()
            return builder.build()
        }
    }
}
