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

import bitsnap.exceptions.UnknownVaryHeader
import bitsnap.http.Header
import org.jetbrains.spek.api.Spek
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class VaryTest : Spek({
    describe("${Vary.name} header") {

        val headerName = Header.headerCompanions.keys
            .asSequence()
            .elementAt(Random().nextInt(Header.headerCompanions.keys.size))

        val testHeader = Vary(headerName)

        it("should be serialized and parsed back") {
            assertEquals("Vary: $headerName", testHeader.toString())
            assertEquals(testHeader, Vary(headerName))
        }

        it("should throw an exception if an unknown header is being targeted") {
            listOf(
                { Vary("dump") },
                { Vary("dump") }
            ).forEach {
                assertFailsWith<UnknownVaryHeader> {
                    it()
                }
            }
        }
    }
})
