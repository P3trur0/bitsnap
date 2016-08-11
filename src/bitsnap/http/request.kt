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
import java.net.URI
import java.util.*

/**
 * HTTP 1.1 Request
 */
open class Request<out T> internal constructor(
    val uri: URI,
    val method: Method,
    val body: Body<T>,
    val headers: List<Header>,
    val version: String = "1.1") {

    open fun methodUrlToString() = "$method $uri"

    override fun toString() =
        "${methodUrlToString()} HTTP/$version\r\n${headers.joinToString { "$it\r\n" }}\r\n$body"

    companion object {
        fun from(requestString: String) {
            print(requestString)
        }
    }
}

abstract class RequestBuilder<out T, B : Body<T>> constructor(val uri: URI, val method: Method) {

    internal var headers: MutableList<Header> = LinkedList()

    abstract internal var body: B?

    open class RequestBuilderException(massage: String) : HttpBuildException(massage)

    class RequestBodyMissingException() : RequestBuilderException("Request body is missing")

    class RequestHeaderAlreadyAssignedException(name: String) : RequestBuilderException("Request header $name have been assigned already")

    class RequestBodyAlreadyAssignedException : RequestBuilderException("Request body have been assigned already")

    @Throws(RequestHeaderAlreadyAssignedException::class)
    fun header(header: Header) {
        if (!header.allowMultiple && headers.filter { it.name == header.name }.size > 0) {
            throw RequestHeaderAlreadyAssignedException(header.name)
        }

        headers.add(header)
    }

    @Throws(RequestBodyAlreadyAssignedException::class)
    protected fun body(body: B) {
        if (this.body == null) {
            this.body = body
        } else {
            throw RequestBodyAlreadyAssignedException()
        }
    }

    internal fun build() = if (body == null) {
        throw RequestBodyMissingException()
    } else {
        Request(uri, method, body!!, headers)
    }
}

internal interface RequestWithMethods<out T, B : Body<T>, out R : Request<T>, out U : RequestBuilder<T, B>> {

    fun build(method: Method, uri: URI, init: U.() -> Unit) : R

    fun Options(uri: URI, init: U.() -> Unit) = build(Method.OPTIONS, uri, init)

    fun Get(uri: URI, init: U.() -> Unit) = build(Method.GET, uri, init)

    fun Head(uri: URI, init: U.() -> Unit) = build(Method.HEAD, uri, init)

    fun Put(uri: URI, init: U.() -> Unit) = build(Method.PUT, uri, init)

    fun Post(uri: URI, init: U.() -> Unit) = build(Method.POST, uri, init)

    fun Delete(uri: URI, init: U.() -> Unit) = build(Method.PUT, uri, init)

    fun Trace(uri: URI, init: U.() -> Unit) = build(Method.TRACE, uri, init)

    fun Connect(uri: URI, init: U.() -> Unit) = build(Method.CONNECT, uri, init)
}

class JsonRequest internal constructor(
    uri: URI,
    method: Method,
    body: Body<JsonNode>,
    headers: List<Header>,
    version: String = "1.1") : Request<JsonNode>(
        uri,
        method,
        body,
        headers,
        version
) {

    class Builder(uri: URI, method: Method) : RequestBuilder<JsonNode, JsonBody>(uri, method) {

        override var body: JsonBody? = null

        @Throws(RequestBodyAlreadyAssignedException::class)
        fun body(node: JsonNode) {
            super.body(JsonBody(node))
        }
    }

    companion object : RequestWithMethods<JsonNode, JsonBody, JsonRequest, JsonRequest.Builder> {
        override fun build(method: Method, uri: URI, init: JsonRequest.Builder.() -> Unit): JsonRequest {
            val builder = JsonRequest.Builder(uri, method)
            builder.init()
            return builder.build() as JsonRequest
        }
    }
}

class PlainRequest internal constructor(
    uri: URI,
    method: Method,
    headers: List<Header>,
    version: String = "1.1") : Request<Unit>(
    uri,
    method,
    EmptyBody,
    headers,
    version
) {
    class Builder(uri: URI, method: Method) : RequestBuilder<Unit, EmptyBody>(uri, method) {
        override var body: EmptyBody? = EmptyBody
    }

    companion object : RequestWithMethods<Unit, EmptyBody, PlainRequest, PlainRequest.Builder> {
        override fun build(method: Method, uri: URI, init: PlainRequest.Builder.() -> Unit): PlainRequest {
            val builder = PlainRequest.Builder(uri, method)
            builder.init()
            return builder.build() as PlainRequest
        }
    }
}


