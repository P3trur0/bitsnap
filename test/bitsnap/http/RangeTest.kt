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

import bitsnap.exceptions.InvalidRangeValue
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RangeTest : Spek({
    describe("Content-Range Ranges") {
        val ranges = listOf(
            "0-7/*",
            "1-/*",
            "-2/*",
            "0-7",
            "1-",
            "-2",
            "0-7/16",
            "1-/16",
            "-2/16"
        )

        val invalidRanges = listOf(
            "-1-2/*",
            "0-7/5",
            "0--1/1",
            "0-1/1",
            "-2/1",
            "2-/1"
        )

        it("should be serialized and parsed back") {
            ranges.forEach {
                val r = Range(it)

                if (it.endsWith("/*")) {
                    assertEquals(it, r.toString())
                    assertEquals(null, r.size)
                } else if (it.contains('/')) {
                    assertEquals(16, r.size)
                }

                when(r) {
                    is Range.Bounded -> {
                        assertEquals(0, r.range.first)
                        assertEquals(7, r.range.last)
                    }
                    is Range.Head -> assertEquals(2, r.prefixEnd)
                    is Range.Tail -> assertEquals(1, r.suffixStart)
                }
            }

            invalidRanges.forEach {
                assertFailsWith<InvalidRangeValue> {
                    Range(it)
                }
            }
        }
    }
})
