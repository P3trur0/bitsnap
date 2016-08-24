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
import java.util.*
import kotlin.test.assertEquals

class AcceptLanguageTest : Spek({

    describe("${AcceptLanguage.name} header") {
        it("should be serialized to string and parsed back") {
            val testLocales = listOf(
                Pair(Locale("en", "US"), 10),
                Pair(Locale("en"), 1),
                Pair(Locale("en", "GB"), 8),
                Pair(Locale("ru", "RU"), 5),
                Pair(Locale("ua", "UA"), 9)
            )

            val testHeader = AcceptLanguage {
                testLocales.forEach {
                    locale(it.first, it.second)
                }
            }

            val localesString = joinToStringWithQuality(testLocales)

            assertEquals("Accept-Language: $localesString", testHeader.toString())
            assertEquals(testHeader, AcceptLanguage(localesString))
        }
    }
})
