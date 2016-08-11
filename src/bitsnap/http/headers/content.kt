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

import bitsnap.http.Header
import bitsnap.http.splitParameters
import java.util.*

class ContentDisposition internal constructor(val type: DispositionType, val parameters: Map<String, String>) : Header() {

    override val name = ContentDisposition.Companion.name

    override val value: String = ""

    operator fun get(key: String) = parameters[key]

    fun filename() = parameters["filename"]

    class ContentDispositionParseException(parameter: String) :
        HeaderParseException("Can't parse Content-Disposition parameter $parameter")

    sealed class DispositionType(val type: String) {
        object Attachment : DispositionType("attachment")

        class DispositionExtension(type: String) : DispositionType(type)

        override fun toString() = type

        companion object {
            fun from(name: String) = if (name == "attachment")
                DispositionType.Attachment
            else
                DispositionType.DispositionExtension(name)
        }
    }

    class ContentDispositionBuilder(val name: String) {

        private val parameters: MutableMap<String, String> = HashMap()

        fun parameter(name: String, value: String) = parameters.put(name, value)

        internal fun build(init: ContentDispositionBuilder.() -> Unit) : ContentDisposition {
            init()
            return ContentDisposition(DispositionType.from(name), parameters)
        }
    }

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(ContentDisposition.Companion)
        }

        override val name = "Content-Disposition"

        @Throws(ContentDispositionParseException::class)
        override fun from(value: String) : ContentDisposition {
            val semIndex = value.indexOf(";")
            if (semIndex < 0) {
                throw ContentDispositionParseException(value)
            }

            val name = value.substring(0, semIndex)
            val parameters = value.substring(semIndex).splitParameters {
                ContentDispositionParseException(it)
            }

            return ContentDisposition(DispositionType.from(name), parameters)
        }

        fun build(name: String, init: ContentDispositionBuilder.() -> Unit) = ContentDispositionBuilder(name).build(init)

        fun attachment(init: ContentDispositionBuilder.() -> Unit) = build("attachment", init)
    }
}

class ContentEncoding internal constructor(override val value: String) : Header() {

    override val name = ContentEncoding.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(ContentEncoding.Companion)
        }

        override val name = "Content-Encoding"
        override fun from(value: String) = ContentEncoding(value)
    }
}

class ContentLanguage internal constructor(override val value: String) : Header() {

    override val name = ContentLanguage.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(ContentLanguage.Companion)
        }

        override val name = "Content-Language"
        override fun from(value: String) = ContentLanguage(value)
    }
}

class ContentLength internal constructor(override val value: String) : Header() {

    override val name = ContentLength.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(ContentLength.Companion)
        }

        override val name = "Content-Length"
        override fun from(value: String) = ContentLength(value)
    }
}

class ContentLocation internal constructor(override val value: String) : Header() {

    override val name = ContentLocation.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(ContentLocation.Companion)
        }

        override val name = "Content-Location"
        override fun from(value: String) = ContentLocation(value)
    }
}

class ContentSecurityPolicy internal constructor(override val value: String) : Header() {

    override val name = ContentSecurityPolicy.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(ContentSecurityPolicy.Companion)
        }

        override val name = "Content-Security-Policy"
        override fun from(value: String) = ContentSecurityPolicy(value)
    }
}

class ContentRange internal constructor(override val value: String) : Header() {

    override val name = ContentRange.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(ContentRange.Companion)
        }

        override val name = "Content-Range"
        override fun from(value: String) = ContentRange(value)
    }
}

class ContentType internal constructor(override val value: String) : Header() {

    override val name = ContentType.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(ContentType.Companion)
        }

        override val name = "Content-Type"
        override fun from(value: String) = ContentType(value)
    }
}

class ContentMD5 internal constructor(override val value: String) : Header() {

    override val name = ContentMD5.Companion.name


    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(ContentMD5.Companion)
        }

        override val name = "Content-MD5"
        override fun from(value: String) = ContentMD5(value)
    }
}

