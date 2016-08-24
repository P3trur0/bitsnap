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

import bitsnap.exceptions.InvalidHeaderException
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class internalTest : Spek({

    describe("MimeType / Content headers parameters parsing") {

        it("should be performed") {
            val params = IntRange(1, 5).associate { Pair("param$it", "value$it") }
            val paramsString = params.entries.joinToString(separator = ";") { " ${it.key}=${it.value}" }
            assertEquals(params, paramsString.splitParameters())

            listOf(
                "$paramsString;///",
                "$paramsString;-=",
                "$paramsString;=-"
            ).forEach {
                assertFailsWith<InvalidHeaderException> { it.splitParameters() }
            }
        }
    }
})
