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

import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals

class ContentTypeTest : Spek({
    describe("${ContentType.name} header") {

        val testTypes = listOf("application/json; param1=1; param2=3",
            "application/xml; param2=2; param1=3",
            "image/png; param=1"
            )

        val testHeaders = testTypes.map {
            ContentType(it)
        }

        it("should be serialized and parsed pack") {
            testHeaders.forEachIndexed { i, testHeader ->
                assertEquals("Content-Type: ${testTypes[i]}", testHeader.toString())
                assertEquals(testHeader, ContentType(testTypes[i]))
            }
        }
    }
})
