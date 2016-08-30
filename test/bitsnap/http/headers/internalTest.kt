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

import bitsnap.exceptions.AlreadyAssignedException
import bitsnap.exceptions.HeaderDuplicateException
import bitsnap.exceptions.InvalidQualityValueException
import bitsnap.http.Charset
import bitsnap.http.Encoding
import bitsnap.http.MimeType
import org.jetbrains.spek.api.Spek
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class internalTest : Spek({
    describe("Splitting Accept quality parameters") {
        it("should be performed") {
            val pairs = IntRange(1, 9).reversed().map { Pair("val$it", it) }
            val pairsString = pairs.joinToString(", ") { "${it.first};q=0.${it.second}" }

            assertEquals(pairsString, joinToStringWithQuality(pairs))
            assertEquals(pairs, pairsString.splitValueQuality().toList())

            assertEquals("val1, val2", joinToStringWithQuality("val1, val2;q=1.0".splitValueQuality().toList()))
        }
    }

    describe("Joining Accept quality parameters") {
        it("should fail with invalid quality") {
            listOf(11, 0).forEach {
                listOf(
                    { AcceptCharset { charset(Charset.UTF8, it) } },
                    { AcceptEncoding { encoding(Encoding.GZIP, it) } },
                    { AcceptLanguage { locale(Locale("en", "US"), it) } },
                    { Accept { type(MimeType("app/xml"), it) } }

                ).forEach {
                    assertFailsWith<InvalidQualityValueException> {
                        it()
                    }
                }
            }
        }

        it("should fail with duplicate values") {
            listOf(
                { AcceptCharset { charsets(Charset.ANSI, Charset.ANSI) } },
                { AcceptEncoding { encodings(Encoding.GZIP, Encoding.GZIP) } },
                { AcceptLanguage { locales(Locale("en"), Locale("en")) } },
                { Accept { types(MimeType("app/xml"), MimeType("app/xml")) } },
                { CacheControl { directives(CacheControl.Directive.Public, CacheControl.Directive.Public) } }
            ).forEach {
                assertFailsWith<AlreadyAssignedException> {
                    it()
                }
            }
        }
    }

    describe("Finding List duplicated") {
        it("should be performed") {
            assertEquals(emptyList(), listOf("A", "B", "C").findDuplicates())
            assertEquals(listOf("B"), listOf("A", "B", "B", "C").findDuplicates())

            assertFailsWith<HeaderDuplicateException> {
                listOf(Pair("A", 0), Pair("A", 1)).checkHeaderPairDuplicates("test")
            }
        }
    }

})
