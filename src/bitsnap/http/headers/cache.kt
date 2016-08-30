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
import bitsnap.http.Header
import java.util.*
import java.util.Date

open class IfMatch internal constructor(val isWeak: Boolean, val tag: String) : Header() {

    internal constructor(pair: Pair<Boolean, String>) : this(pair.first, pair.second)

    constructor(tag: String) : this(false, tag)

    override val name = IfMatch.Companion.name

    override val value by lazy {
        if (isWeak) {
            "\\W\"$tag\""
        } else {
            "\"$tag\""
        }
    }

    fun isAny() = tag == "*"

    override fun equals(other: Any?) = if (other is IfMatch) {
        isWeak == other.isWeak && tag == other.tag
    } else false

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(IfMatch.Companion)
        }

        fun weak(tag: String) = IfMatch(true, tag)
        fun strong(tag: String) = IfMatch(false, tag)

        override val name = "If-Match"

        override operator fun invoke(value: String) = IfMatch(splitWeak(value))

        internal fun splitWeak(value: String): Pair<Boolean, String> = when {
            value.startsWith("\\W") -> Pair(true, value.removePrefix("\\W").trim('"'))
            else -> Pair(false, value.trim('"'))
        }
    }
}

class ETag internal constructor(isWeak: Boolean, tag: String) : IfMatch(isWeak, tag) {

    internal constructor(pair: Pair<Boolean, String>) : this(pair.first, pair.second)

    override val name = ETag.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(ETag.Companion)
        }

        fun weak(tag: String) = ETag(true, tag)
        fun strong(tag: String) = ETag(false, tag)

        override val name = "ETag"

        override operator fun invoke(value: String) = ETag(splitWeak(value))
    }
}

class Expires constructor(val date: Date) : Header() {

    override val name = Expires.Companion.name

    override val value: String by lazy {
        formatDate(date)
    }

    override fun equals(other: Any?) = if (other is Expires) {
        date == other.date
    } else false

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(Expires.Companion)
        }

        override val name = "Expires"
        override operator fun invoke(value: String) = Expires(parseDate(value))
    }
}

class CacheControl internal constructor(val directives: List<DirectiveType>) : Header() {

    override val name = CacheControl.Companion.name

    override val value by lazy {
        directives.joinToString(", ")
    }

    override fun equals(other: Any?) = if (other is CacheControl) {
        directives == other.directives
    } else false

    sealed class DirectiveType(val name: String) {

        abstract val value: String

        abstract class Plain(name: String) : DirectiveType(name) {
            override val value = ""

            override fun equals(other: Any?) = if (other is Plain) {
                this.name == other.name
            } else false
        }

        abstract class Age(name: String, val seconds: Int) : DirectiveType(name) {
            init {
                if (!isValid()) {
                    throw InvalidCacheControlException(this.toString())
                }
            }

            override val value by lazy {
                seconds.toString()
            }

            fun isValid() = seconds >= 0

            override fun equals(other: Any?) = if (other is Age) {
                this.seconds == other.seconds
            } else false

            override fun toString() = "$name=$value"
        }

        abstract class Field(name: String) : DirectiveType(name) {
            override val value = ""

            override fun equals(other: Any?) = if (other is Field) {
                this.name == other.name
            } else false
        }

        override fun toString() = if (value.isNotEmpty()) {
            "$name=\"$value\""
        } else {
            "$name"
        }
    }

    object Directive {
        class NoCache(override val value: String) : DirectiveType.Field("no-cache") {
            constructor() : this("")
        }

        class Private(override val value: String) : DirectiveType.Field("private") {
            constructor() : this("")
        }

        class Extension(name: String, override val value: String) : DirectiveType.Field(name) {
            init {
                if (value.isBlank()) {
                    throw InvalidCacheControlException(value)
                }
            }
        }

        class MaxAge(seconds: Int) : DirectiveType.Age("max-age", seconds)

        class SMaxAge(seconds: Int) : DirectiveType.Age("s-maxage", seconds)

        class MaxStale(seconds: Int) : DirectiveType.Age("max-stale", seconds)

        class MinFresh(seconds: Int) : DirectiveType.Age("min-fresh", seconds)

        object Public : DirectiveType.Plain("public")

        object NoStore : DirectiveType.Plain("no-store")

        object NoTransform : DirectiveType.Plain("no-transform")

        object OnlyIfCached : DirectiveType.Plain("only-if-cached")

        object MustRevalidate : DirectiveType.Plain("must-revalidate")

        object ProxyRevalidate : DirectiveType.Plain("proxy-revalidate")

        operator fun invoke(value: String) : DirectiveType = try {
            when {
            // Fields
                value == "no-cache=" -> Directive.NoCache()
                value.startsWith("no-cache=") -> Directive.NoCache(
                    value.removePrefix("no-cache=").trim('"'))

                value== "private" -> Directive.Private()
                value.startsWith("private=") -> Directive.Private(
                    value.removePrefix("private=").trim('"'))

            // Age
                value.startsWith("max-age=") -> Directive.MaxAge(
                    value.removePrefix("max-age=").trim('"').toInt())
                value.startsWith("s-maxage=") -> Directive.SMaxAge(
                    value.substring("s-maxage=".length).trim('"').toInt())
                value.startsWith("max-stale=") -> Directive.MaxStale(
                    value.substring("max-stale=".length).trim('"').toInt())
                value.startsWith("min-fresh=") -> Directive.MinFresh(
                    value.removePrefix("min-fresh=").trim('"').toInt())

            // Plain
                value == "public" -> Directive.Public
                value == "no-store" -> Directive.NoStore
                value== "no-transform" -> Directive.NoTransform
                value == "only-if-cached" -> Directive.OnlyIfCached
                value== "must-revalidate" -> Directive.MustRevalidate
                value== "proxy-revalidate" -> Directive.ProxyRevalidate

                else -> {
                    val eqIndex = value.indexOf('=')
                    if (eqIndex > 0) {
                        val (name, directive) = value.split('=')
                        Directive.Extension(name, directive.trim('"'))
                    } else {
                        throw UnknownCacheControlException(value)
                    }
                }
            }
        } catch (e: NumberFormatException) {
            throw InvalidCacheControlException(value)
        }
    }

    data class Builder internal constructor(private val directives: MutableList<DirectiveType> = LinkedList()) {
        
        fun directive(directive: DirectiveType) {
            if (this.directives.firstOrNull { it.name == directive.name } == null) {
                directives.add(directive)
            } else throw AlreadyAssignedException("Cache-Control directive ${directive.name}")
        }

        fun directives(vararg directives: DirectiveType) {
            directives.forEach {
                directive(it)
            }
        }

        companion object {

            @Throws(HeaderDuplicateException::class)
            operator fun invoke(init: Builder.() -> Unit): CacheControl {
                val builder = Builder()
                builder.init()
                builder.directives.checkHeaderDuplicates(CacheControl.name)
                return CacheControl(builder.directives)
            }
        }
    }

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(CacheControl.Companion)
        }

        override val name = "Cache-Control"

        @Throws(InvalidCacheControlException::class)
        override operator fun invoke(value: String): CacheControl {

            val directives = value.split(',').asSequence()
                .map { it.trim() }
                .map { Directive(it) }.toList()

            return CacheControl(directives)
        }

        @Throws(HeaderDuplicateException::class)
        operator fun invoke(init: Builder.() -> Unit) = Builder(init)
    }
}

class Vary constructor(override val value: String) : Header() {

    init {
        if (!Header.headerCompanions.containsKey(value)) {
            throw UnknownVaryHeader(value)
        }
    }

    override val name = Vary.Companion.name

    override fun equals(other: Any?) = if (other is Vary) {
        value == other.value
    } else false

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(Vary.Companion)
        }

        override val name = "Vary"

        @Throws(UnknownVaryHeader::class)
        override operator fun invoke(headerName: String) = if (Header.headerCompanions.containsKey(headerName.toLowerCase())) {
            Vary(headerName)
        } else {
            throw UnknownVaryHeader(headerName)
        }
    }
}

class IfModifiedSince constructor(val date: Date) : Header() {

    override val name = IfModifiedSince.Companion.name

    override val value by lazy {
        formatDate(date)
    }

    override fun equals(other: Any?) = if (other is IfModifiedSince) {
        date == other.date
    } else false

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(IfModifiedSince.Companion)
        }

        override val name = "If-Modified-Since"

        override operator fun invoke(value: String) = IfModifiedSince(parseDate(value))
    }
}

class IfNoneMatch internal constructor(isWeak: Boolean, tag: String) : IfMatch(isWeak, tag) {

    internal constructor(pair: Pair<Boolean, String>) : this(pair.first, pair.second)

    constructor(tag: String) : this(false, tag)

    override val name = IfNoneMatch.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(IfNoneMatch.Companion)
        }

        fun weak(tag: String) = IfNoneMatch(true, tag)
        fun strong(tag: String) = IfNoneMatch(false, tag)

        override val name = "If-None-Match"

        override operator fun invoke(value: String) = IfNoneMatch(splitWeak(value))
    }
}

open class IfRange internal constructor(isWeak: Boolean, tag: String) : IfMatch(isWeak, tag) {

    override val name = IfRange.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(IfRange.Companion)
        }

        override val name = "If-Range"
        override operator fun invoke(value: String): IfRange = try {
            val date = parseDate(value)
            IfRangeDate(date)
        } catch (e: HeaderDateParseException) {
            IfRangeTag(value)
        }
    }
}

class IfRangeTag internal constructor(isWeak: Boolean, tag: String) : IfRange(isWeak, tag) {

    internal constructor(pair: Pair<Boolean, String>) : this(pair.first, pair.second)

    constructor(tag: String) : this(false, tag)

    companion object {

        fun weak(tag: String) = IfRangeTag(true, tag)

        fun strong(tag: String) = IfRangeTag(false, tag)

        operator fun invoke(value: String) = IfRangeTag(splitWeak(value))
    }
}

class IfRangeDate constructor(val date: Date) : IfRange(false, "") {
    override val value by lazy {
        Header.formatDate(date)
    }

    override fun equals(other: Any?) = if (other is IfRangeDate) {
        date == other.date
    } else false

    companion object {
        operator fun invoke(value: String) = IfRangeDate(parseDate(value))
    }
}

class IfUnmodifiedSince constructor(val date: Date) : Header() {

    override val name = IfUnmodifiedSince.Companion.name

    override val value by lazy {
        Header.formatDate(date)
    }

    override fun equals(other: Any?) = if (other is IfUnmodifiedSince) {
        date == other.date
    } else false

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(IfUnmodifiedSince.Companion)
        }

        override val name = "If-Unmodified-Since"

        override operator fun invoke(value: String) = IfUnmodifiedSince(parseDate(value))
    }
}

