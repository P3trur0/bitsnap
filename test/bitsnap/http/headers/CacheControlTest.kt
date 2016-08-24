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

class CacheControlTest : Spek({
    describe("${CacheControl.name} header") {

        val testDirectives = listOf(
            CacheControl.Directive.NoCache("test"),
            CacheControl.Directive.Private("test"),
            CacheControl.Directive.MaxAge(1),
            CacheControl.Directive.SMaxAge(2),
            CacheControl.Directive.MaxStale(3),
            CacheControl.Directive.MinFresh(4),
            CacheControl.Directive.Public,
            CacheControl.Directive.NoStore,
            CacheControl.Directive.NoTransform,
            CacheControl.Directive.OnlyIfCached,
            CacheControl.Directive.MustRevalidate,
            CacheControl.Directive.ProxyRevalidate
        )

        val testHeader = CacheControl {
            testDirectives.forEach {
                directive(it)
            }
        }

        val directivesString = testDirectives.joinToString(", ")

        it("should be serialized and parsed back") {
            assertEquals("Cache-Control: $directivesString", testHeader.toString())
            assertEquals(testHeader, CacheControl(directivesString))
        }
    }
})
