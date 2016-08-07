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
import kotlin.reflect.companionObjectInstance

abstract class Header {

    open class HeaderParseException(message: String) : HttpParseException(message)

    abstract val name: String

    abstract val value: String

    override fun toString() = "$name: $value"

    companion object {

        internal interface HeaderCompanion {
            val name: String

            fun from(value: String): Header
        }

        internal val headers: Map<String, HeaderCompanion> by lazy {
            ClassPath.of(ClassLoader.getSystemClassLoader()).packageClasses("bitsnap.http.headers")
                .map{it.kotlinClass.companionObjectInstance}
                .filter { it is HeaderCompanion }
                .map { it as HeaderCompanion }
                .associateBy({ it.name }, {it})
        }

        fun from(name: String, value: String) = headers[name]?.from(value) ?: Header.Unknown(name, value)

        fun from(headerString: String) : Header {
            val sepIndex = headerString.indexOf(": ")
            if (sepIndex < 0) {
                throw HeaderParseException("Can't parse header $headerString")
            }

            val name = headerString.substring(0, sepIndex)
            val value = headerString.substring(sepIndex)

            return from(name, value)
        }
    }

    open val allowMultiple = false

    class Unknown(override val name: String, override val value: String) : Header()
}
