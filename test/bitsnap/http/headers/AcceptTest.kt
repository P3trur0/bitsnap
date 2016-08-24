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

package bitsnap.http.headers

import bitsnap.http.MimeType
import org.jetbrains.spek.api.Spek
import java.util.*
import kotlin.test.assertEquals

class AcceptTest : Spek({
    describe("${Accept.name} header") {

        val types = listOf(
            Pair("application/xml", 10),
            Pair("image/png", 2),
            Pair("application/json", 7),
            Pair("video/mpg", 1)
        ).map {
            Pair(MimeType(it.first), it.second)
        }

        val testHeader = Accept {
            val shuffledTypes: MutableList<Pair<MimeType, Int>> = ArrayList(types.size)
            shuffledTypes.addAll(types)
            Collections.shuffle(shuffledTypes)

            shuffledTypes.forEach { type(it.first, it.second) }
        }

        val typesString = joinToStringWithQuality(types)
        val headerString = "Accept: $typesString"

        it("should be serialized") {
            assertEquals(headerString, testHeader.toString())
        }

        it("should be parsed ignoring spaces and non-quality parameters") {
            assertEquals(testHeader, Accept(typesString))
            assertEquals(headerString,
                Accept("application/xml, application/json; attr=X; q = 0.7, " +
                    "image/png;stuff; q =0.2 ; attr=X, video/mpg; q = 0.1")
                    .toString())
        }
    }
})
