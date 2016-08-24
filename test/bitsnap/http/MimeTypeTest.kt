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

import bitsnap.exceptions.InvalidMimeTypeException
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MimeTypeTest : Spek({

    fun typeFromPair(pair: Pair<String, String>) = MimeType("${pair.first}/${pair.second}")

    describe("MimeType") {

        it("should be parsed without parameters") {
            listOf(
                Pair("application", "json"),
                Pair("application-a", "a*")
            ).forEach {
                val type = typeFromPair(it)
                assertEquals(it.first, type.type)
                assertEquals(it.second, type.subType)
            }

            listOf(
                Pair("application/", "json"),
                Pair("application-a", "@a-")
            ).forEach {
                assertFailsWith<InvalidMimeTypeException> {
                    typeFromPair(it)
                }
            }
        }

        it("should be parsed with parameters") {
            val params = IntRange(1, 3).associate { Pair("param$it", "value$it") }
            val type = MimeType("application/json; ${
            params.entries.joinToString(separator = ";") { " ${it.key}=${it.value}" }
            }")

            assertEquals("application", type.type)
            assertEquals("json", type.subType)
            assertEquals(params, type.parameters)
        }
    }
})
