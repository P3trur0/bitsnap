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

interface Body {

    fun contents(): String

    interface Parser {
        fun from(bodyString: String): Body

        open class BodyParseException(message: String) : Throwable(message)
    }

    object EmptyBody : Body {
        override fun contents() = ""
    }

    data class StringBody(val body: String) : Body {
        override fun contents() = body

        class Parser : Body.Parser {
            override fun from(bodyString: String) = StringBody(bodyString)
        }

        fun toJson() = JsonBody.Parser().from(body)
    }

    data class JsonBody(val node: JsonNode) : Body {
        override fun contents() = node.toString()

        class Parser : Body.Parser {

            class JsonParseException : Body.Parser.BodyParseException("Can't parse JSON")

            var mapper = ObjectMapper()

            override fun from(bodyString: String) = try {
                JsonBody(mapper.readTree(bodyString))
            } catch (e: JsonProcessingException) {
                throw JsonParseException()
            }
        }
    }
}
