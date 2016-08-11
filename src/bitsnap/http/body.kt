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

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

abstract class Body<out T> {
    abstract val contents: T

    override fun toString() = contents.toString()

    open class BodyParseException(message: String) : HttpParseException(message)

    companion object {
        fun string(string: String) = StringBody(string)
        fun json(string: String) = JsonBody.from(string)
        fun empty() = EmptyBody
    }
}

object EmptyBody : Body<Unit>() {
    override val contents : Unit = Unit
    override fun toString() = ""
}

class StringBody(override val contents: String) : Body<String>() {
    fun toJsonBody() = JsonBody.from(this)
}

class JsonBody internal constructor(override val contents: JsonNode): Body<JsonNode>() {

    class JsonParseException : BodyParseException("Can't parse JSON")

    fun toStringBody() = StringBody(contents.toString())

    companion object {

        var mapper = ObjectMapper()

        @Throws(JsonParseException::class)
        fun from(string: String) = try {
            JsonBody(mapper.readTree(string))
        } catch (e: JsonProcessingException) {
            throw JsonParseException()
        }

        @Throws(JsonParseException::class)
        fun from(body: StringBody) = try {
            JsonBody(mapper.readTree(body.contents))
        } catch (e: JsonProcessingException) {
            throw JsonParseException()
        }
    }
}
