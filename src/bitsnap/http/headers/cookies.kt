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

import bitsnap.exceptions.AlreadyAssignedException
import bitsnap.exceptions.InvalidCookie
import bitsnap.http.Header
import bitsnap.http.toIntOrThrow
import java.util.Date

class Cookie internal constructor(val cookies: Map<String, String>) : Header() {

    override val name = Cookie.Companion.name

    override val value by lazy {
        cookies.entries.joinToString("; ") { "${it.key}=${it.value}" }
    }

    override fun equals(other: Any?) = if (other is Cookie) {
        other.cookies == cookies
    } else false

    override val allowMultiple = true

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(Cookie.Companion)
        }

        override val name = "Cookie"

        override operator fun invoke(value: String) = Cookie(value.split(";").map { it.trim() }.associate {
                val sepIndex = it.indexOf('=')
                if (sepIndex > 0) {
                    val name = it.substring(0, sepIndex)
                    val value = it.substring(sepIndex+1)
                    Pair(name, value)
                } else throw InvalidCookie(value)
            })
    }
}

class SetCookie internal constructor(
    val cookieName: String,
    val cookieValue: String,
    val path: String?,
    val domain: String?,
    val expires: Date?,
    val maxAge: Int?,
    val secure: Boolean,
    val httpOnly: Boolean
) : Header() {

    override val name = SetCookie.Companion.name

    override val value by lazy {
        val builder = StringBuilder()
        builder.append("$cookieName=$cookieValue")

        if (path != null) builder.append("; Path=$path")
        if (domain != null) builder.append("; Domain=$domain")
        if (expires != null) builder.append("; Expires=${Header.formatDate(expires)}")
        if (maxAge != null) builder.append("; Max-Age=$maxAge")
        if (secure) builder.append("; Secure")
        if (httpOnly) builder.append("; HttpOnly")

        builder.toString()
    }

    override fun equals(other: Any?) = if (other is SetCookie) {
        other.cookieName == cookieName && other.cookieValue == cookieValue
    } else false

    override val allowMultiple = true

    data class Builder(
        var path: String? = null,
        var domain: String? = null,
        var expires: Date? = null,
        var maxAge: Int? = null,
        var secure: Boolean = false,
        var httpOnly: Boolean = false
    ) {

        fun path(path: String) {
            if (this.path == null) {
                this.path = path
            } else throw AlreadyAssignedException("Cookie path") }

        fun domain(domain: String) = if (this.domain == null) {
            this.domain = domain
        } else throw AlreadyAssignedException("Cookie path")

        fun expires(expires: Date) = if (this.expires == null) {
            this.expires = expires
        } else throw AlreadyAssignedException("Cookie expires")

        fun maxAge(maxAge: Int) = if (this.maxAge == null) {
            this.maxAge = maxAge
        } else throw AlreadyAssignedException("Cookie Max-Age")

        fun secure() { this.secure = true }
        fun httpOnly() { this.httpOnly = true }

        companion object {
            operator fun invoke(name: String, value: String, init: Builder.() -> Unit) : SetCookie {
                val builder = Builder()
                builder.init()
                return SetCookie(
                    name, value,
                    builder.path,
                    builder.domain,
                    builder.expires,
                    builder.maxAge,
                    builder.secure,
                    builder.httpOnly
                )
            }
        }
    }

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(SetCookie.Companion)
        }

        override val name = "Set-Cookie"

        operator fun invoke(name: String, value: String, init: Builder.() -> Unit) = Builder.invoke(name, value, init)

        override operator fun invoke(value: String) : SetCookie {
            val chunks = value.split(';')
            val semIndex = chunks[0].indexOf('=')
            if (semIndex > 0) {
                val cookieName = chunks[0].substring(0, semIndex).trim()
                val cookieValue = chunks[0].substring(semIndex + 1).trim()

                return Builder(cookieName, cookieValue) {
                    chunks.forEachIndexed { idx, chunk ->
                        if (idx > 0) {
                            val trimmedChunk = chunk.trim()

                            when {
                                trimmedChunk.startsWith("Path=") -> path(trimmedChunk
                                    .removePrefix("Path="))
                                trimmedChunk.startsWith("Domain=") -> domain(trimmedChunk
                                    .removePrefix("Domain="))
                                trimmedChunk.startsWith("Expires=") -> expires(Header.parseDate(trimmedChunk
                                    .removePrefix("Expires=")))
                                trimmedChunk.startsWith("Max-Age=") -> maxAge(trimmedChunk
                                    .removePrefix("Max-Age=")
                                    .toIntOrThrow(InvalidCookie(value)))
                                trimmedChunk == "HttpOnly" -> httpOnly()
                                trimmedChunk == "Secure" -> secure()
                            }
                        }
                    }
                }

            } else throw InvalidCookie(value)
        }
    }
}
