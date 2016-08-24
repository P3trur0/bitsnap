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

import bitsnap.http.Encoding
import org.jetbrains.spek.api.Spek
import java.util.*
import kotlin.test.assertEquals

class AcceptEncodingTest : Spek({

    describe("${AcceptEncoding.name} header") {
        val rand = Random()
        val encodingTypes = Encoding.values().map {
            Pair(it, rand.nextInt(10) + 1)
        }

        val testHeader = AcceptEncoding {
            encodingTypes.forEach {
                encoding(it.first, it.second)
            }
        }

        val typesString = joinToStringWithQuality(encodingTypes)

        it("should be serialized and parsed back") {
            assertEquals("Accept-Encoding: $typesString", testHeader.toString())
            assertEquals(testHeader, AcceptEncoding(typesString))
        }
    }
})
