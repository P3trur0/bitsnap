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

import bitsnap.exceptions.HeaderDuplicateException
import bitsnap.exceptions.InvalidContentDispositionException
import bitsnap.exceptions.InvalidContentLengthException
import bitsnap.exceptions.InvalidMD5Value
import bitsnap.http.*
import bitsnap.http.Range
import java.util.*

class ContentDisposition internal constructor(val type: DispositionType, val parameters: Map<String, String>) : Header() {

    override val name = ContentDisposition.Companion.name

    override val value by lazy {
        "$type; ${parameters.entries.joinToString(", ") { "${it.key}=${it.value}" }}"
    }

    override fun equals(other: Any?) = if (other is ContentDisposition) {
        type == other.type && parameters == other.parameters
    } else false

    operator fun get(key: String) = parameters[key]

    fun filename() = parameters["filename"]

    sealed class DispositionType(val name: String) {
        object Attachment : DispositionType("attachment")

        class DispositionExtension(type: String) : DispositionType(type)

        override fun equals(other: Any?) = if (other is DispositionType) {
            name == other.name
        } else false

        override fun toString() = name

        companion object {
            operator fun invoke(name: String) = if (name == "attachment")
                DispositionType.Attachment
            else
                DispositionType.DispositionExtension(name)
        }
    }

    class Builder internal constructor(private val parameters: MutableMap<String, String> = HashMap()) {

        fun parameter(name: String, value: String) = parameters.put(name, value)

        fun filename(value: String) = parameters.put("filename", value)

        companion object {

            @Throws(HeaderDuplicateException::class)
            operator fun invoke(name: String, init: Builder.() -> Unit): ContentDisposition {
                val builder = ContentDisposition.Builder()
                builder.init()
                return ContentDisposition(DispositionType(name), builder.parameters)
            }
        }
    }

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(ContentDisposition.Companion)
        }

        override val name = "Content-Disposition"

        @Throws(InvalidContentDispositionException::class)
        override operator fun invoke(value: String): ContentDisposition {
            val semIndex = value.indexOf(";")

            if (semIndex > 0) {
                val name = value.substring(0, semIndex).trim()
                val parameters = value.substring(semIndex + 1).splitParametersOrThrowWith(",") {
                    InvalidContentDispositionException(it)
                }

                return ContentDisposition(DispositionType(name), parameters)
            }

            return ContentDisposition(DispositionType(value.trim()), emptyMap())
        }

        operator fun invoke(name: String, init: Builder.() -> Unit) = Builder(name, init)

        fun attachment(init: Builder.() -> Unit) = Builder("attachment", init)
    }
}

class ContentEncoding internal constructor(val encoding: Encoding) : Header() {

    override val name = ContentEncoding.Companion.name

    override val value by lazy {
        encoding.toString()
    }

    override fun equals(other: Any?) = if (other is ContentEncoding) {
        encoding == other.encoding
    } else false

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(ContentEncoding.Companion)
        }

        override val name = "Content-Encoding"

        override operator fun invoke(value: String) = ContentEncoding(Encoding(value))
    }
}

class ContentLanguage internal constructor(val locales: List<Locale>) : Header() {

    override val name = ContentLanguage.Companion.name

    override val value by lazy {
        locales.joinToString(", ")
    }

    override fun equals(other: Any?) = if (other is ContentLanguage) {
        locales == other.locales
    } else false

    data class Builder internal constructor(private val locales: MutableList<Locale> = LinkedList()) {

        fun locale(locale: Locale) = locales.add(locale)

        fun locales(vararg locales: Locale) = this.locales.addAll(locales)

        companion object {

            @Throws(HeaderDuplicateException::class)
            operator fun invoke(init: Builder.() -> Unit): ContentLanguage {
                val builder = Builder()
                builder.init()
                builder.locales.checkHeaderDuplicates(ContentLanguage.name)
                return ContentLanguage(builder.locales)
            }
        }
    }

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(ContentLanguage.Companion)
        }

        override val name = "Content-Language"

        override operator fun invoke(value: String) = ContentLanguage(
            value.split(",").map {
                val sepIndex = it.indexOfAny(listOf("-", "_"))
                if (sepIndex > 0) {
                    val language = it.substring(0, sepIndex).trim()
                    val country = it.substring(sepIndex + 1).trim()
                    Locale(language, country)
                } else {
                    Locale(it.trim())
                }
            }
        )

        @Throws(HeaderDuplicateException::class)
        operator fun invoke(init: Builder.() -> Unit) = Builder(init)
    }
}

class ContentLength(val length: Int) : Header() {

    init {
        if (length <= 0) {
            throw InvalidContentLengthException(length.toString())
        }
    }

    override val name = ContentLength.Companion.name

    override val value by lazy {
        length.toString()
    }

    override fun equals(other: Any?) = if (other is ContentLength) {
        length == other.length
    } else false

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(ContentLength.Companion)
        }

        override val name = "Content-Length"

        override operator fun invoke(value: String) =
            ContentLength(value.toIntOrThrow(InvalidContentLengthException(value)))
    }
}

class ContentLocation internal constructor(val url: URL) : Header() {

    override val name = ContentLocation.Companion.name

    override val value by lazy {
        url.toString()
    }

    override fun equals(other: Any?) = if (other is ContentLocation) {
        url == other.url
    } else false

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(ContentLocation.Companion)
        }

        override val name = "Content-Location"

        override operator fun invoke(value: String) = ContentLocation(URL(value))
    }
}

class ContentRange internal constructor(val range: Range, val unit: RangeUnit) : Header() {

    override val name = ContentRange.Companion.name

    override val value by lazy {
        if (unit == RangeUnit.None) {
            "$unit"
        } else {
            "$unit $range"
        }
    }

    override fun equals(other: Any?) = if (other is ContentRange) {
        range == other.range && unit == other.unit
    } else false

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(ContentRange.Companion)
        }

        override val name = "Content-Range"

        override operator fun invoke(value: String) : ContentRange {
            val chunks = value.split(' ')
            return if (chunks.size == 2) {
                ContentRange(Range(chunks[1]), RangeUnit(chunks[0]))
            } else {
                ContentRange(Range.None, RangeUnit(chunks[0]))
            }
        }
    }
}

class ContentType internal constructor(val type: MimeType) : Header() {

    override val name = ContentType.Companion.name

    override val value by lazy {
        type.toString()
    }

    override fun equals(other: Any?) = if (other is ContentType) {
        type == other.type
    } else false

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(ContentType.Companion)
        }

        override val name = "Content-Type"
        override operator fun invoke(value: String) = ContentType(MimeType(value))
    }
}

class ContentMD5 internal constructor(override val value: String) : Header() {

    init {
        if (!value.matches(md5regex)) throw InvalidMD5Value(value)
    }

    override val name = ContentMD5.Companion.name

    override fun equals(other: Any?) = if (other is ContentMD5) {
        value == other.value
    } else false

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(ContentMD5.Companion)
        }

        internal val md5regex = "([a-fA-F0-9]{32})".toRegex()

        override val name = "Content-MD5"
        override operator fun invoke(value: String) = ContentMD5(value)
    }
}
