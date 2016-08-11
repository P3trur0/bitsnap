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
import bitsnap.http.HttpException
import java.util.*
import java.util.Date

class ETag internal constructor(val isWeak: Boolean, val tag: String) : Header() {

    override val name = ETag.Companion.name

    override val value by lazy {
        if (isWeak) {
            "\\W$tag"
        } else {
            tag
        }
    }

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(ETag.Companion)
        }

        fun weak(tag: String) = ETag(true, tag)

        fun strong(tag: String) = ETag(false, tag)

        override val name = "ETag"
        override fun from(value: String) = when {
            value.startsWith("\\W") -> ETag(true, value.removePrefix("\\W"))
            else -> ETag(false, value)
        }
    }
}

class Expires constructor(val date: Date) : Header() {

    override val name = Expires.Companion.name

    override val value : String by lazy {
        formatDate(date)
    }

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(Expires.Companion)
        }

        override val name = "Expires"
        override fun from(value: String) = Expires(parseDate(value))
    }
}

class CacheControl internal constructor(directives: List<DirectiveType>) : Header() {

    constructor(directive: DirectiveType) : this(listOf(directive))

    override val name = CacheControl.Companion.name

    override val value by lazy {
        directives.joinToString(", ")
    }

    class CacheControlParseValueException(value: String) : HeaderParseException("Can't parse cache Cache-Control value $value")

    sealed class DirectiveType(val name: String) {

        abstract val value : String

        abstract class Plain(name: String) : DirectiveType(name) {
            override val value = ""

            override fun equals(other: Any?) = if (other is Plain) {
                this.name == other.name
            } else false
        }

        abstract class Age(name: String, val seconds: Int) : DirectiveType(name) {
            override val value by lazy {
                seconds.toString()
            }

            fun isValid() = seconds >= 0

            override fun equals(other: Any?) = if (other is Age) {
                this.seconds == other.seconds
            } else false
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

    class Directive {
        class NoCache(override val value: String) : DirectiveType.Field("no-cache") {
            constructor() : this("")
        }

        class Private(override val value: String) : DirectiveType.Field("private") {
            constructor() : this("")
        }

        class Extension(name: String, override val value: String) : DirectiveType.Field(name) {
            constructor(name: String) : this(name, "")
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
    }

    class CacheControlBuilder {

        val directives: MutableList<DirectiveType> = LinkedList()

        fun directive(directive: DirectiveType) {
            directives.add(directive)
        }

        internal fun build(init: CacheControlBuilder.() -> Unit): CacheControl {
            this.init()
            return CacheControl(directives)
        }
    }

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(CacheControl.Companion)
        }

        override val name = "Cache-Control"

        @Throws(CacheControlParseValueException::class)
        override fun from(value: String) : CacheControl {

            val directives = value.split(';')
                .map{ it.trim() }
                .map {
                when {
                    value.startsWith("no-cache") -> if (value["no-cache=".length] == '=') {
                        Directive.NoCache(value.substring("no-cache".length + 1))
                    } else {
                        Directive.NoCache()
                    }

                    value.startsWith("private") -> if (value["private=".length] == '=') {
                        Directive.Private(value.substring("private=".length))
                    } else {
                        Directive.Private()
                    }
                    value.startsWith("max-age=") -> try {
                        Directive.MaxAge(value.substring("max-age=".length).toInt())
                    } catch (e: NumberFormatException) {
                        throw CacheControlParseValueException(value)
                    }
                    value.startsWith("s-maxage=") -> try {
                        Directive.SMaxAge(value.substring("s-maxage=".length).toInt())
                    } catch (e: NumberFormatException) {
                        throw CacheControlParseValueException(value)
                    }
                    value.startsWith("max-stale=") -> try {
                        Directive.MaxStale(value.substring("max-stale=".length).toInt())
                    } catch (e: NumberFormatException) {
                        throw CacheControlParseValueException(value)
                    }
                    value.startsWith("min-fresh=") -> try {
                        Directive.MinFresh(value.substring("min-fresh=".length).toInt())
                    } catch (e: NumberFormatException) {
                        throw CacheControlParseValueException(value)
                    }
                    value == "public" -> Directive.Public
                    value == "no-store" -> Directive.NoStore
                    value == "no-transform" -> Directive.NoTransform
                    value == "only-if-cached" -> Directive.OnlyIfCached
                    value == "must-revalidate" -> Directive.MustRevalidate
                    value == "proxy-revalidate" -> Directive.ProxyRevalidate

                    else -> {
                        val eqIndex = value.indexOf('=')
                        if (eqIndex > 0 && value.isNotEmpty()) {
                            val (name, directive) = value.split('=')
                            Directive.Extension(name, directive.trim('"'))
                        } else {
                            throw CacheControlParseValueException(value)
                        }
                    }
                }
            }

            return CacheControl(directives)
        }

        fun build(init: CacheControlBuilder.() -> Unit) = CacheControlBuilder().build(init)
    }
}

class Vary constructor(override val value: String) : Header() {

    class UnknownVaryHeader(name: String) : HttpException("Unknown Vary Header $name")

    init {
        @Throws(UnknownVaryHeader::class)
        if (!Header.headerCompanions.containsKey(value)) {
            throw UnknownVaryHeader(value)
        }
    }

    override val name = Vary.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(Vary.Companion)
        }

        override val name = "Vary"

        @Throws(UnknownVaryHeader::class)
        override fun from(headerName: String) = if (Header.headerCompanions.containsKey(headerName)) {
            Vary(headerName)
        } else {
            throw UnknownVaryHeader(headerName)
        }
    }
}

class IfMatch internal constructor(val isWeak: Boolean, val tag: String) : Header() {

    constructor(tag: String) : this(false, tag)

    override val name = IfMatch.Companion.name

    override val value by lazy {
        if (isWeak) {
            "\\W$tag"
        } else {
            tag
        }
    }

    fun any() = value == "*"

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(IfMatch.Companion)
        }

        fun weak(tag: String) = IfMatch(true, tag)

        fun strong(tag: String) = IfMatch(false, tag)

        override val name = "If-Match"
        override fun from(value: String) = when {
            value.startsWith("\\W") -> IfMatch(true, value.removePrefix("\\W"))
            else -> IfMatch(false, value)
        }
    }
}

class IfModifiedSince constructor(val date: Date) : Header() {

    override val name = IfModifiedSince.Companion.name

    override val value by lazy {
        formatDate(date)
    }

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(IfModifiedSince.Companion)
        }

        override val name = "If-Modified-Since"
        override fun from(value: String) = IfModifiedSince(parseDate(value))
    }
}

class IfNoneMatch internal constructor(val isWeak: Boolean, val tag: String) : Header() {

    constructor(tag: String) : this(false, tag)

    override val name = IfNoneMatch.Companion.name

    override val value by lazy {
        if (isWeak) {
            "\\W$tag"
        } else {
            tag
        }
    }

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(IfNoneMatch.Companion)
        }

        fun weak(tag: String) = IfMatch(true, tag)

        fun strong(tag: String) = IfMatch(false, tag)

        override val name = "If-None-Match"
        override fun from(value: String) = when {
            value.startsWith("\\W") -> IfNoneMatch(true, value.removePrefix("\\W"))
            else -> IfNoneMatch(false, value)
        }
    }
}

abstract class IfRange : Header() {

    override val name = IfRange.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(IfRange.Companion)
        }

        override val name = "If-Range"
        override fun from(value: String) : IfRange = try {
            val date = parseDate(value)
            IfRangeDate(date)
        } catch (e: HeaderDateParseException) {
            IfRangeTag.from(value)
        }
    }
}

class IfRangeTag internal constructor(val isWeak: Boolean, val tag: String) : IfRange() {

    constructor(tag: String): this(false, tag)

    override val value by lazy {
        if (isWeak) {
            "\\W$tag"
        } else {
            tag
        }
    }

    companion object {

        fun weak(tag: String) = IfRangeTag(true, tag)

        fun strong(tag: String) = IfRangeTag(false, tag)

        fun from(value: String) = when {
            value.startsWith("\\W") -> IfRangeTag(true, value.removePrefix("\\W"))
            else -> IfRangeTag(false, value)
        }
    }
}

class IfRangeDate constructor(val date: Date) : IfRange() {
    override val value by lazy {
        IfModifiedSince.formatDate(date)
    }
}

class IfUnmodifiedSince constructor(val date: Date) : Header() {

    override val name = IfUnmodifiedSince.Companion.name

    override val value by lazy {
        IfModifiedSince.formatDate(date)
    }

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(IfUnmodifiedSince.Companion)
        }

        override val name = "If-Unmodified-Since"
        override fun from(value: String) = IfUnmodifiedSince(parseDate(value))
    }
}

