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

class IfNoneMatchTest : Spek({
    describe("${IfNoneMatch.name} header") {

        val weakTag = IfNoneMatch.weak("test")
        val weakString = "\\W\"test\""

        val strongTag = IfNoneMatch("test")
        val strongString = "\"test\""

        it("should be serialized and parsed back") {
            assertEquals("If-None-Match: $weakString", weakTag.toString())
            assertEquals("If-None-Match: $strongString", strongTag.toString())

            assertEquals(weakTag, IfNoneMatch.weak("test"))
            assertEquals(strongTag, IfNoneMatch("test"))
        }
    }
})
