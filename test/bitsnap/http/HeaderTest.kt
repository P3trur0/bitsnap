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

import bitsnap.reflect.ClassPath
import org.jetbrains.spek.api.Spek
import kotlin.reflect.companionObject
import kotlin.reflect.companionObjectInstance
import kotlin.test.assertEquals

const val headersPackage = "bitsnap.http.headers"

class HeaderTest : Spek({

    describe("Header in $headersPackage package") {

        val headerAbstractVariants = listOf( // abstract descendants, have no HeaderCompanion
            "IfRangeDate",
            "IfRangeTag"
        )

        val classpath = ClassPath.of(ClassLoader.getSystemClassLoader())

        val headers = classpath.packageClasses(headersPackage)
            .filter { !it.className.endsWith("Kt") }
            .filter { !it.className.endsWith("Test") }

        val abstractVariantCompanionPairs = headers
            .filter { headerAbstractVariants.contains(it.className) }
            .map { Pair(it, it.kotlinClass.companionObjectInstance) }

        val resourceCompanionPairs = headers
            .filter { !headerAbstractVariants.contains(it.className) }
            .map { Pair(it, it.kotlinClass.companionObjectInstance as Header.HeaderCompanion) }

        it("should have a companion object") {
            assertEquals(emptyList(),
                headers.filter { it.kotlinClass.companionObjectInstance == null }.map {
                    it.className
                }
            )
        }

        it("abstract variants should not implement companion interface") {
            assertEquals(emptyList(),
                abstractVariantCompanionPairs
                    .filter { it.first.kotlinClass.companionObject is Header.HeaderCompanion }
                    .map { it.first.className }
            )
        }

        it("should implement Header companion interface") {
            resourceCompanionPairs.forEach {
                val (resource, companion) = it
                if (!listOf(// list of classnames that don't match with header's name
                    "DoNotTrack",
                    "TrackingStatus",
                    "TransferEncodings",
                    "XSSProtection"
                ).contains(resource.className)) {
                    assertEquals(companion.name.toLowerCase().replace("-", ""), resource.className.toLowerCase())
                }
            }
        }

        it("should be registered") {
            val registeredHeaders = Header.headerCompanions.values.map { it.name }.sorted()
            val actualHeaders = resourceCompanionPairs.map { it.second.name }.sorted()

            assertEquals(registeredHeaders, actualHeaders)
        }

        it("should have a respective test case") {
            val testCases = classpath.packageClasses(headersPackage)
                .filter { it.className.endsWith("Test") }
                .filter { !it.className.startsWith("internal") }
                .map { it.className.removeSuffix("Test") }
                .sorted()

            val headers = classpath.packageClasses(headersPackage)
                .filter { !headerAbstractVariants.contains(it.className) }
                .filter { !it.className.endsWith("Kt") }
                .filter { !it.className.endsWith("Test") }
                .map { it.className }
                .sorted()

            assertEquals(headers, testCases)
        }
    }
})
