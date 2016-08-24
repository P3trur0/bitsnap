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

import bitsnap.http.Header
import org.jetbrains.spek.api.Spek
import java.util.Date
import kotlin.test.assertEquals

class IfRangeTest : Spek({
    describe("${IfRange.name} header") {

        val now = Date()
        val dateString = Header.formatDate(now)
        val testDateHeader: IfRange = IfRange(dateString) as IfRange

        val weakTag : IfRange = IfRangeTag.weak("test")
        val weakString = "\\W\"test\""

        val strongTag : IfRange = IfRangeTag("test")
        val strongString = "\"test\""

        it("should be serialized and parsed back") {
            assertEquals("If-Range: $dateString", testDateHeader.toString())
            assertEquals(testDateHeader, IfRange(dateString))

            assertEquals("If-Range: $weakString", weakTag.toString())
            assertEquals("If-Range: $strongString", strongTag.toString())

            assertEquals(weakTag, IfRangeTag.weak("test"))
            assertEquals(strongTag, IfRangeTag("test"))
        }
    }
})
