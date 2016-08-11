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
import bitsnap.http.MimeType
import java.nio.charset.Charset
import java.util.*
import java.util.Date



class Accept internal constructor(val types: List<Pair<MimeType, Int>>) : Header() {

    constructor(vararg types: MimeType) : this(types.map { Pair(it, 10) })

    override val name = Accept.Companion.name

    internal fun joinMimeTypeToStringWithQuality(types: List<Pair<MimeType, Int>>) =
        types.joinToString {
            "${it.first.typeToString()}${
            if (it.second < 10 && it.second >= 0)
                ";q=0.${it.second}}"
            else ""
            }, "
        }.removeSuffix(", ")

    override val value: String by lazy {
        joinMimeTypeToStringWithQuality(types)
    }

    class AcceptBuilder {

        val types: MutableList<Pair<MimeType, Int>> = LinkedList()

        fun type(type: MimeType) = types.add(Pair(type, 10))

        fun type(type: MimeType, quality: Int) = types.add(Pair(type, quality))

        fun type(type: MimeType, quality: Float) = types.add(Pair(type, Math.floor(quality.toDouble() * 10).toInt()))

        internal fun build(init: AcceptBuilder.() -> Unit): Accept {
            this.init()
            return Accept(types)
        }
    }

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(Accept.Companion)
        }

        override val name = "Accept"

        override fun from(value: String) = Accept(value.splitValueQuality().map {
            val (stringValue, quality) = it
            Pair(MimeType.from(stringValue), quality)
        })

        fun build(init: AcceptBuilder.() -> Unit) = AcceptBuilder().build(init)
    }
}

class AcceptCharset internal constructor(val charsets: List<Pair<Charset, Int>>) : Header() {

    constructor(vararg charsets: Charset) : this(charsets.map { Pair(it, 10) })

    override val name = AcceptCharset.Companion.name

    override val value: String by lazy {
        joinToStringWithQuality(charsets)
    }

    class AcceptCharsetBuilder {

        val charsets: MutableList<Pair<Charset, Int>> = LinkedList()

        fun charset(ch: Charset) = charsets.add(Pair(ch, 10))

        fun charset(ch: Charset, quality: Int) = charsets.add(Pair(ch, quality))

        internal fun build(init: AcceptCharsetBuilder.() -> Unit): AcceptCharset {
            this.init()
            return AcceptCharset(charsets)
        }
    }

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(AcceptCharset.Companion)
        }

        override val name = "Accept-Charset"

        override fun from(value: String) = AcceptCharset(Charset.forName(value))

        fun build(init: AcceptCharsetBuilder.() -> Unit) = AcceptCharsetBuilder().build(init)
    }
}

class AcceptEncoding internal constructor(val types: List<Pair<EncodingType, Int>>) : Header() {

    constructor(vararg types: AcceptEncoding.EncodingType) : this(types.map { Pair(it, 10) })

    override val name = AcceptEncoding.Companion.name

    override val value: String by lazy {
        joinToStringWithQuality(types)
    }

    enum class EncodingType(val type: String) {
        // Any encoding
        ANY("*"),
        // Deprecated compress
        COMPRESS("compress"),
        // Old Plain Deflate
        DEFLATE("deflate"),
        // W3C Efficient XML Interchange
        EXI("exi"),
        GZIP("gzip"),
        // No transformation
        IDENTITY("identity"),
        // Network Transfer Format for Java Archives
        PACK200_GZIP("pack200-gzip"),
        // Brotli
        BR("br"),
        BZIP2("bzip2"),
        LZMA("lzma"),
        PEERDIST("peerdist"),
        SDCH("sdch"),
        XPRESS("xpress"),
        XZ("xz");

        override fun toString() = type

        companion object {

            @Throws(HeaderParseException::class)
            fun from(value: String) = EncodingType.values().find { it.type == value }
                ?: throw HeaderParseException("Unknown $value encoding")
        }
    }

    class AcceptEncodingBuilder {

        val encodings: MutableList<Pair<EncodingType, Int>> = LinkedList()

        fun encoding(ch: EncodingType) =encodings.add(Pair(ch, 10))

        fun encoding(ch: EncodingType, quality: Int) = encodings.add(Pair(ch, quality))

        internal fun build(init: AcceptEncoding.AcceptEncodingBuilder.() -> Unit): AcceptEncoding {
            this.init()
            return AcceptEncoding(encodings)
        }
    }

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(AcceptEncoding.Companion)
        }

        override val name = "Accept-Encoding"

        override fun from(value: String): AcceptEncoding {
            return AcceptEncoding(value.splitValueQuality().map {
                val (encodingTypeValue, quality) = it
                Pair(AcceptEncoding.EncodingType.from(encodingTypeValue), quality)
            })
        }

        fun build(init: AcceptEncoding.AcceptEncodingBuilder.() -> Unit) = AcceptEncodingBuilder().build(init)
    }
}

class AcceptLanguage internal constructor(val locales: List<Pair<Locale, Int>>) : Header() {

    constructor(vararg locales: Locale) : this(locales.map { Pair(it, 10) })

    override val name = AcceptLanguage.Companion.name

    override val value: String by lazy {
        joinToStringWithQuality(locales)
    }

    class AcceptLanguageBuilder {
        val locales: MutableList<Pair<Locale, Int>> = LinkedList()

        fun locale(locale: Locale) = locales.add(Pair(locale, 10))

        fun locale(locale: Locale, quality: Int) = locales.add(Pair(locale, quality))

        internal fun build(init: AcceptLanguage.AcceptLanguageBuilder.() -> Unit): AcceptLanguage {
            this.init()
            return AcceptLanguage(locales)
        }
    }

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(AcceptLanguage.Companion)
        }

        override val name = "Accept-Language"

        override fun from(value: String): AcceptLanguage {
            return AcceptLanguage(value.splitValueQuality().map {
                val (stringValue, quality) = it
                val sepIndex = stringValue.indexOf('-')

                if (sepIndex > 0) {
                    val (language, country) = stringValue.split("-")
                    Pair(Locale(language, country), quality)
                } else {
                    Pair(Locale(value), quality)
                }
            })
        }

        fun build(init: AcceptLanguageBuilder.() -> Unit) = AcceptLanguage.AcceptLanguageBuilder().build(init)
    }
}

class AcceptDatetime(val date: Date) : Header() {

    override val name = AcceptDatetime.Companion.name

    override val value: String by lazy {
        formatDate(date)
    }

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(AcceptDatetime.Companion)
        }

        override val name = "Accept-Datetime"

        override fun from(value: String) = AcceptDatetime(parseDate(value))
    }
}

class AcceptPatch internal constructor(val types: List<MimeType>) : Header() {

    override val name = AcceptPatch.Companion.name

    override val value: String by lazy {
        types.joinToString(", ")
    }

    class AcceptPatchBuilder {
        val types: MutableList<MimeType> = LinkedList()
        fun type(type: MimeType) = types.add(type)

        internal fun build(init: AcceptPatchBuilder.() -> Unit): AcceptPatch {
            this.init()
            return AcceptPatch(types)
        }
    }

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(AcceptPatch.Companion)
        }

        override val name = "Accept-Patch"

        override fun from(value: String) = AcceptPatch(value.split(", ").map { MimeType.from(it) })

        fun build(init: AcceptPatchBuilder.() -> Unit) = AcceptPatchBuilder().build(init)
    }
}

class AcceptRanges(val unit: RangeUnit) : Header() {

    override val name = AcceptRanges.Companion.name

    override val value: String by lazy {
        unit.toString()
    }

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(AcceptRanges.Companion)
        }

        override val name = "Accept-Ranges"

        override fun from(value: String) = AcceptRanges(RangeUnit.from(value))
    }
}
