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

import bitsnap.http.toHexString
import org.jetbrains.spek.api.Spek
import java.security.MessageDigest
import kotlin.test.assertEquals

class ContentMD5Test : Spek({
    describe("${ContentMD5.name} header") {

        val hashSums = listOf(
            "TEST1",
            "TEST2",
            "TEST3"
        ).map {
            val digest = MessageDigest.getInstance("MD5")
            digest.digest(it.toByteArray()).toHexString()
        }

        val testHeaders = hashSums.map { ContentMD5(it) }

        it("should be serialized and parsed back") {
            testHeaders.forEachIndexed { i, testHeader ->
                assertEquals("Content-MD5: ${hashSums[i]}", testHeader.toString())
                assertEquals(testHeader, ContentMD5(hashSums[i]))
            }
        }
    }
})
