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
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.Date

internal fun <T> List<Pair<T, Int>>.joinToStringWithQuality(proc: (T) -> String) =
    this.joinToString {
        "${proc(it.first)}${if (it.second < 10 && it.second >= 0) ";q=0.${it.second}" else ""}, ".removeSuffix(", ")
    }

internal fun <T> List<Pair<T, Int>>.joinToStringWithQuality() = this.joinToStringWithQuality { it.toString() }

internal fun String.splitValueQuality() = this.split(", ").map {
    val qualityIndex = it.indexOfAny(listOf(";q=", "; q="))
    return@map if (qualityIndex > 0) {
        val value = (it.substring(0, qualityIndex))
        var qualityString = it.substring(qualityIndex)
        // there might be some other parameters
        val qualitySemIndex = qualityString.indexOf(';')
        if (qualitySemIndex > 0) {
            qualityString = qualityString.substring(0, qualitySemIndex)
        }

        try {
            val quality = (qualityString.toFloat() * 10).toInt()
            Pair(value, quality)
        } catch(e: NumberFormatException) {
            throw Header.HeaderParseException("Cant parse header quality $qualityString")
        }
    } else {
        Pair(it, 10)
    }
}

class Accept internal constructor(val types: List<Pair<MimeType, Int>>) : Header() {

    constructor(vararg types: MimeType) : this(types.map { Pair(it, 10) })

    override val name = Accept.Companion.name

    override val value: String by lazy {
        types.joinToStringWithQuality()
    }

    class AcceptBuilder {

        val types: MutableList<Pair<MimeType, Int>> = ArrayList()

        fun type(type: MimeType) {
            types.add(Pair(type, 10))
        }

        fun type(type: MimeType, quality: Int) {
            types.add(Pair(type, quality))
        }

        internal fun build(init: AcceptBuilder.() -> Unit): Accept {
            this.init()
            return Accept(types)
        }
    }

    companion object : HeaderCompanion {

        override val name = "Accept"

        override fun from(value: String) = Accept(value.splitValueQuality().map {
            val (stringValue, quality) = it
            Pair(MimeType.from(stringValue), quality)
        })

        fun types(init: AcceptBuilder.() -> Unit) = AcceptBuilder().build(init)
    }
}

class AcceptCharset internal constructor(val charsets: List<Pair<Charset, Int>>) : Header() {

    constructor(vararg charsets: Charset) : this(charsets.map { Pair(it, 10) })

    override val name = AcceptCharset.Companion.name

    override val value: String by lazy {
        charsets.joinToStringWithQuality()
    }

    class AcceptCharsetBuilder {

        val charsets: MutableList<Pair<Charset, Int>> = ArrayList()

        fun charset(ch: Charset) {
            charsets.add(Pair(ch, 10))
        }

        fun charset(ch: Charset, quality: Int) {
            charsets.add(Pair(ch, quality))
        }

        internal fun build(init: AcceptCharsetBuilder.() -> Unit): AcceptCharset {
            this.init()
            return AcceptCharset(charsets)
        }
    }

    companion object : HeaderCompanion {

        override val name = "Accept-Charset"

        override fun from(value: String) = AcceptCharset(Charset.forName(value))

        fun charsets(init: AcceptCharsetBuilder.() -> Unit) = AcceptCharsetBuilder().build(init)
    }
}

class AcceptEncoding internal constructor(val types: List<Pair<EncodingType, Int>>) : Header() {

    constructor(vararg types: AcceptEncoding.EncodingType) : this(types.map { Pair(it, 10) })

    override val name = AcceptEncoding.Companion.name

    override val value: String by lazy {
        types.joinToStringWithQuality()
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
            fun from(value: String) = EncodingType.values().find { it.type == value }
                ?: throw HeaderParseException("Unknown $value encoding")
        }
    }

    class AcceptEncodingBuilder {

        val encodings: MutableList<Pair<EncodingType, Int>> = ArrayList()

        fun encoding(ch: EncodingType) {
            encodings.add(Pair(ch, 10))
        }

        fun encoding(ch: EncodingType, quality: Int) {
            encodings.add(Pair(ch, quality))
        }

        internal fun build(init: AcceptEncoding.AcceptEncodingBuilder.() -> Unit): AcceptEncoding {
            this.init()
            return AcceptEncoding(encodings)
        }
    }

    companion object : HeaderCompanion {

        override val name = "Accept-Encoding"

        override fun from(value: String): AcceptEncoding {
            return AcceptEncoding(value.splitValueQuality().map {
                val (encodingTypeValue, quality) = it
                Pair(AcceptEncoding.EncodingType.from(encodingTypeValue), quality)
            })
        }

        fun encodings(init: AcceptEncoding.AcceptEncodingBuilder.() -> Unit) = AcceptEncodingBuilder().build(init)
    }
}

class AcceptLanguage internal constructor(val locales: List<Pair<Locale, Int>>) : Header() {

    constructor(vararg locales: Locale) : this(locales.map { Pair(it, 10) })

    override val name = AcceptLanguage.Companion.name

    override val value: String by lazy {
        locales.joinToStringWithQuality()
    }

    class AcceptLanguageBuilder {
        val locales: MutableList<Pair<Locale, Int>> = ArrayList()

        fun locale(locale: Locale) {
            locales.add(Pair(locale, 10))
        }

        fun locale(locale: Locale, quality: Int) {
            locales.add(Pair(locale, quality))
        }

        internal fun build(init: AcceptLanguage.AcceptLanguageBuilder.() -> Unit): AcceptLanguage {
            this.init()
            return AcceptLanguage(locales)
        }
    }

    companion object : HeaderCompanion {

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

        fun locales(init: AcceptLanguage.AcceptLanguageBuilder.() -> Unit) = AcceptLanguage.AcceptLanguageBuilder().build(init)
    }
}

class AcceptDatetime(val date: Date) : Header() {

    override val name = AcceptDatetime.Companion.name

    override val value: String by lazy {
        dateFormat.format(date.toInstant().atZone(ZoneId.of("GMT")))
    }

    companion object : HeaderCompanion {

        private val dateFormat = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneId.of("GMT"))

        override val name = "Accept-Datetime"

        override fun from(value: String) = AcceptDatetime(Date.from(ZonedDateTime.parse(value, dateFormat).toInstant()))
    }
}

class AcceptPatch(val types: List<MimeType>) : Header() {

    override val name = AcceptPatch.Companion.name

    override val value: String by lazy {
        types.joinToString(", ")
    }

    companion object : HeaderCompanion {
        override val name = "Accept-Patch"

        override fun from(value: String) = AcceptPatch(value.split(", ").map { MimeType.from(it) })
    }
}

class AcceptRanges(val unit: RangeUnit) : Header() {

    override val name = AcceptRanges.Companion.name

    override val value: String by lazy {
        unit.toString()
    }

    sealed class RangeUnit {
        object Bytes : RangeUnit() {
            override fun toString() = "bytes"
        }

        class Specific(val name: String) : RangeUnit() {
            override fun toString() = name
        }

        companion object {
            fun from(value: String) = when (value) {
                "bytes" -> RangeUnit.Bytes
                else -> Specific(value)
            }
        }
    }

    companion object : HeaderCompanion {
        override val name = "Accept-Ranges"

        override fun from(value: String) = AcceptRanges(RangeUnit.from(value))
    }
}
