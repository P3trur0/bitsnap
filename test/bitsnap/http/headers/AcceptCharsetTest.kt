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

import bitsnap.http.Charset
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals

class AcceptCharsetTest : Spek({

    describe("${AcceptCharset.name} header") {

        val charsets = listOf(
            Pair(Charset.ASCII, 1),
            Pair(Charset.ANSI, 2),
            Pair(Charset.ISO8859, 8),
            Pair(Charset.UTF8, 3),
            Pair(Charset.UTF16, 10),
            Pair(Charset.UTF32, 4)
        )

        val testHeader = AcceptCharset {
            charsets.forEach {
                charset(it.first, it.second)
            }
        }

        val charsetsString = joinToStringWithQuality(charsets)

        it("should be serialized and parsed back") {
            assertEquals("Accept-Charset: $charsetsString", testHeader.toString())
            assertEquals(testHeader, AcceptCharset(charsetsString))
        }
    }
})