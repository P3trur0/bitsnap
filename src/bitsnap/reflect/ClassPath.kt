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

import kotlin.reflect.KClass

data class ClassPath internal constructor(val classes: List<ClassResource>) {

    data class ClassResource internal constructor(val name: String) {

        val javaClass : Class<*> by lazy {
            Class.forName(name)
        }

        val kotlinClass : KClass<*> by lazy {
            javaClass.kotlin
        }

        val className : String by lazy {
            name.substringAfterLast(".")
        }

        val packageName : String by lazy {
            name.substringBeforeLast(".")
        }
    }

    fun packageClasses(packageName: String) = classes.filter {
        it.packageName.startsWith(packageName) &&
            !it.packageName.substring(packageName.length).contains('.') &&
            !it.className.contains("Test")
    }

    companion object {
        fun of(loader: ClassLoader) = ClassPath(Scanner.resources(loader))
    }
}
