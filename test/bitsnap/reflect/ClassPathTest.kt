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

package bitsnap.reflect

import bitsnap.reflect.test.TestCompanionInterface
import org.jetbrains.spek.api.Spek
import kotlin.reflect.companionObjectInstance
import kotlin.test.assertEquals

class ClassPathTest : Spek({
    val classpath = ClassPath.of(ClassLoader.getSystemClassLoader())

    describe("ClassPath reflection") {

        it("should retrieve bitsnap.reflect.test classes") {
            assertEquals(2, classpath.packageClasses("bitsnap.reflect")
                .filter { !it.className.endsWith("Test") }.size)

            val classes = classpath.packageClasses("bitsnap.reflect.test")
                .filter { !it.className.endsWith("Interface") }

            assertEquals(3, classes.size)
            assertEquals(listOf("A", "B", "C").map { "${it}Reflection" }, classes.map { it.className })
            classes.map { it.kotlinClass.companionObjectInstance }.forEach {
                assert(it is TestCompanionInterface)
            }
        }
    }
})
