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

import bitsnap.exceptions.*
import bitsnap.http.*
import java.util.*
import java.util.Date

class Accept internal constructor(val types: List<Pair<MimeType, Int>>) : Header() {

    constructor(type: MimeType) : this(listOf(Pair(type, 10)))

    override val name = Accept.Companion.name

    override val value: String by lazy {
        joinToStringWithQuality(types) {
            it.typeToString()
        }
    }

    override fun equals(other: Any?) = if (other is Accept) {
        types == other.types
    } else false

    data class Builder internal constructor(private val types: MutableList<Pair<MimeType, Int>> = LinkedList()) {

        private fun type(type: Pair<MimeType, Int>) = checkQuality(Accept.name, type.second) {
            if (types.firstOrNull() { it.first == type.first } != null) {
                throw AlreadyAssignedException("MimeType ${type.first.typeToString()}")
            } else types.add(type)
        }

        fun type(type: MimeType, quality: Int) = type(Pair(type, quality))

        fun type(type: MimeType) = type(Pair(type, 10))

        fun types(vararg types: MimeType) = types.forEach {
            type(Pair(it, 10))
        }

        companion object {

            @Throws(HeaderDuplicateException::class)
            operator fun invoke(init: Builder.() -> Unit): Accept {
                val builder = Builder()
                builder.init()
                builder.types.checkHeaderPairDuplicates(Accept.name)
                return Accept(builder.types
                    .sortedWith(acceptQualityComparator)
                )
            }
        }
    }

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(Accept.Companion)
        }

        override val name = "Accept"

        override operator fun invoke(value: String) = Accept(value.splitValueQuality().map {
            val (stringValue, quality) = it
            Pair(MimeType(stringValue), quality)
        }.toList())

        @Throws(HeaderDuplicateException::class)
        operator fun invoke(init: Builder.() -> Unit) = Builder(init)
    }
}

class AcceptCharset internal constructor(val charsets: List<Pair<Charset, Int>>) : Header() {

    constructor(charset: Charset) : this(listOf(Pair(charset, 10)))

    override val name = AcceptCharset.Companion.name

    override val value: String by lazy {
        joinToStringWithQuality(charsets)
    }

    override fun equals(other: Any?) = if (other is AcceptCharset) {
        charsets == other.charsets
    } else false

    data class Builder internal constructor(private val charsets: MutableList<Pair<Charset, Int>> = LinkedList()) {

        private fun charset(charset: Pair<Charset, Int>) = checkQuality(AcceptCharset.name, charset.second) {
            if (charsets.firstOrNull() { it.first == charset.first } != null) {
                throw AlreadyAssignedException("Charset ${charset.first.toString()}")
            } else charsets.add(charset)
        }

        fun charset(charset: Charset, quality: Int) = charset(Pair(charset, quality))

        fun charset(charset: Charset) = charset(charset, 10)

        fun charsets(vararg charsets: Charset) = charsets.forEach {
            charset(Pair(it, 10))
        }

        companion object {

            @Throws(HeaderDuplicateException::class)
            operator fun invoke(init: Builder.() -> Unit): AcceptCharset {
                val builder = Builder()
                builder.init()
                builder.charsets.checkHeaderPairDuplicates(AcceptCharset.name)
                return AcceptCharset(builder.charsets
                    .sortedWith(acceptQualityComparator)
                )
            }
        }
    }

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(AcceptCharset.Companion)
        }

        override val name = "Accept-Charset"

        @Throws(UnknownCharsetException::class)

        override operator fun invoke(value: String) = AcceptCharset(value.splitValueQuality().map {
            Pair(Charset(it.first), it.second)
        }.toList())

        @Throws(HeaderDuplicateException::class)
        operator fun invoke(init: Builder.() -> Unit) = Builder(init)
    }
}

class AcceptEncoding internal constructor(val types: List<Pair<Encoding, Int>>) : Header() {

    constructor(type: Encoding) : this(listOf(Pair(type, 10)))

    override val name = AcceptEncoding.Companion.name

    override val value: String by lazy {
        joinToStringWithQuality(types)
    }

    override fun equals(other: Any?) = if (other is AcceptEncoding) {
        types == other.types
    } else false

    data class Builder internal constructor(private val encodings: MutableList<Pair<Encoding, Int>> = LinkedList()) {

        private fun encoding(encoding: Pair<Encoding, Int>) = checkQuality(AcceptEncoding.name, encoding.second) {
            if (encodings.firstOrNull() { it.first == encoding.first } != null) {
                throw AlreadyAssignedException("Encoding ${encoding.first.toString()}")
            } else encodings.add(encoding)
        }

        fun encoding(encoding: Encoding, quality: Int) = encoding(Pair(encoding, quality))

        fun encoding(encoding: Encoding) = encoding(encoding, 10)

        fun encodings(vararg encodings: Encoding) = encodings.forEach {
            encoding(Pair(it, 10))
        }

        companion object {
            @Throws(HeaderDuplicateException::class)
            operator fun invoke(init: Builder.() -> Unit): AcceptEncoding {
                val builder = Builder()
                builder.init()
                builder.encodings.checkHeaderPairDuplicates(AcceptEncoding.name)
                return AcceptEncoding(builder.encodings
                    .sortedWith(acceptQualityComparator)
                )
            }
        }
    }

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(AcceptEncoding.Companion)
        }

        override val name = "Accept-Encoding"

        @Throws(UnknownEncodingException::class)
        override operator fun invoke(value: String): AcceptEncoding {
            return AcceptEncoding(value.splitValueQuality().map {
                val (encodingTypeValue, quality) = it
                Pair(Encoding(encodingTypeValue), quality)
            }.toList())
        }

        @Throws(HeaderDuplicateException::class)
        operator fun invoke(init: Builder.() -> Unit) = Builder(init)
    }
}

class AcceptLanguage internal constructor(val locales: List<Pair<Locale, Int>>) : Header() {

    constructor(locale: Locale) : this(listOf(Pair(locale, 10)))

    override val name = AcceptLanguage.Companion.name

    override val value: String by lazy {
        joinToStringWithQuality(locales)
    }

    override fun equals(other: Any?) = if (other is AcceptLanguage) {
        locales == other.locales
    } else false

    data class Builder internal constructor(private val locales: MutableList<Pair<Locale, Int>> = LinkedList()) {

        private fun locale(locale: Pair<Locale, Int>) = checkQuality(AcceptLanguage.name, locale.second) {
            if (locales.firstOrNull() { it.first == locale.first } != null) {
                throw AlreadyAssignedException("Encoding ${locale.first.toString()}")
            } else locales.add(locale)
        }

        fun locale(locale: Locale, quality: Int) = locale(Pair(locale, quality))

        fun locale(locale: Locale) = locale(locale, 10)

        fun locales(vararg locales: Locale) = locales.forEach {
            locale(Pair(it, 10))
        }

        companion object {

            @Throws(HeaderDuplicateException::class)
            operator fun invoke(init: Builder.() -> Unit): AcceptLanguage {
                val builder = Builder()
                builder.init()
                builder.locales.checkHeaderPairDuplicates(AcceptLanguage.name)
                return AcceptLanguage(builder.locales
                    .sortedWith(acceptQualityComparator)
                )
            }
        }
    }

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(AcceptLanguage.Companion)
        }

        override val name = "Accept-Language"

        override operator fun invoke(value: String): AcceptLanguage {
            return AcceptLanguage(value.splitValueQuality().map {
                val (stringValue, quality) = it
                val sepIndex = stringValue.indexOfAny(listOf("-", "_"))

                if (sepIndex > 0) {
                    val language = stringValue.substring(0, sepIndex)
                    val country = stringValue.substring(sepIndex + 1)
                    Pair(Locale(language, country), quality)
                } else {
                    Pair(Locale(stringValue), quality)
                }
            }.toList())
        }

        @Throws(HeaderDuplicateException::class)
        operator fun invoke(init: Builder.() -> Unit) = Builder(init)
    }
}

class AcceptDatetime(val date: Date) : Header() {

    override val name = AcceptDatetime.Companion.name

    override val value: String by lazy {
        formatDate(date)
    }

    override fun equals(other: Any?) = if (other is AcceptDatetime) {
        date.equals(other.date)
    } else false

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(AcceptDatetime.Companion)
        }

        override val name = "Accept-Datetime"

        @Throws(HeaderDateParseException::class)
        override operator fun invoke(value: String) = AcceptDatetime(parseDate(value))
    }
}

class AcceptPatch internal constructor(val types: List<MimeType>) : Header() {

    constructor(type: MimeType) : this(listOf(type))

    override val name = AcceptPatch.Companion.name

    override val value: String by lazy {
        types.joinToString(", ")
    }

    override fun equals(other: Any?) = if (other is AcceptPatch) {
        types == other.types
    } else false

    data class Builder internal constructor(private val types: MutableList<MimeType> = LinkedList()) {

        fun type(type: MimeType) = types.add(type)

        fun types(vararg types: MimeType) = this.types.addAll(types)

        companion object {
            operator fun invoke(init: Builder.() -> Unit): AcceptPatch {
                val builder = Builder()
                builder.init()
                builder.types.checkHeaderDuplicates(AcceptPatch.name)
                return AcceptPatch(builder.types)
            }
        }
    }

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(AcceptPatch.Companion)
        }

        override val name = "Accept-Patch"

        override operator fun invoke(value: String) = AcceptPatch(value.split(", ").map { MimeType(it) })

        operator fun invoke(init: Builder.() -> Unit) = Builder(init)
    }
}

class AcceptRanges(val unit: RangeUnit) : Header() {

    override val name = AcceptRanges.Companion.name

    override val value: String by lazy {
        unit.toString()
    }

    override fun equals(other: Any?) = if (other is AcceptRanges) {
        unit == other.unit
    } else false

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(AcceptRanges.Companion)
        }

        override val name = "Accept-Ranges"

        override operator fun invoke(value: String) = AcceptRanges(RangeUnit(value))
    }
}
