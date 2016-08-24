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

import bitsnap.exceptions.AllowMethodParseException
import bitsnap.http.Header
import bitsnap.http.Method
import bitsnap.http.RangeUnit
import java.time.ZoneId
import java.time.ZonedDateTime
import bitsnap.http.Status as HttpStatus
import java.util.Date as JavaDate

class Allow internal constructor(val methods: List<Method>) : Header() {

    override val name = Authorization.Companion.name


    override val value by lazy {
        methods.joinToString(", ")
    }

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(Allow.Companion)
        }

        override val name = "Allow"
        override operator fun invoke(value: String) =
            Allow(value.split(",")
                .map { it.trim() }
                .map {
                    try {
                        Method.valueOf(it)
                    } catch (e: Throwable) { // TODO: Exception ?
                        throw AllowMethodParseException(it)
                    }
                })
    }
}

class Authorization internal constructor(override val value: String) : Header() {

    override val name = Authorization.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(Authorization.Companion)
        }

        override val name = "Authorization"
        override operator fun invoke(value: String) = Authorization(value)
    }
}

class Connection internal constructor(override val value: String) : Header() {

    override val name = Connection.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(Connection.Companion)
        }

        override val name = "Connection"
        override operator fun invoke(value: String) = Connection(value)
    }
}

class Date internal constructor(val date: JavaDate) : Header() {

    override val name = Date.Companion.name

    override val value: String by lazy {
        dateFormat.format(date.toInstant().atZone(ZoneId.of("GMT")))
    }

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(Date.Companion)
        }

        override val name = "Date"

        override operator fun invoke(value: String) = Date(JavaDate.from(ZonedDateTime.parse(value, dateFormat).toInstant()))
    }
}

class Expect internal constructor(val status: HttpStatus) : Header() {

    override val name = Expect.Companion.name

    override val value by lazy {
        "${status.value()}-${status.toString().toLowerCase()}"
    }

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(Expect.Companion)
        }

        override val name = "Expect"
        override operator fun invoke(value: String) = Expect(HttpStatus(value))
    }
}

class From internal constructor(override val value: String) : Header() {

    override val name = From.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(From.Companion)
        }

        override val name = "From"
        override operator fun invoke(value: String) = From(value)
    }
}

class Host internal constructor(override val value: String) : Header() {

    override val name = Host.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(Host.Companion)
        }

        override val name = "Host"
        override operator fun invoke(value: String) = Host(value)
    }
}

class LastModified internal constructor(override val value: String) : Header() {

    override val name = LastModified.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(LastModified.Companion)
        }

        override val name = "Last-Modified"
        override operator fun invoke(value: String) = LastModified(value)
    }
}

class Link internal constructor(override val value: String) : Header() {

    override val name = Link.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(Link.Companion)
        }

        override val name = "Link"
        override operator fun invoke(value: String) = Link(value)
    }
}

class Location internal constructor(override val value: String) : Header() {

    override val name = Location.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(Location.Companion)
        }

        override val name = "Location"
        override operator fun invoke(value: String) = Location(value)
    }
}

class Origin internal constructor(override val value: String) : Header() {

    override val name = Origin.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(Origin.Companion)
        }

        override val name = "Origin"
        override operator fun invoke(value: String) = Origin(value)
    }
}

class P3P internal constructor(override val value: String) : Header() {

    override val name = P3P.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(P3P.Companion)
        }

        override val name = "P3P"
        override operator fun invoke(value: String) = P3P(value)
    }
}

class Permanent internal constructor(override val value: String) : Header() {

    override val name = Permanent.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(Permanent.Companion)
        }

        override val name = "Permanent"
        override operator fun invoke(value: String) = Permanent(value)
    }
}

class Pragma internal constructor(override val value: String) : Header() {

    override val name = Pragma.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(Pragma.Companion)
        }

        override val name = "Pragma"
        override operator fun invoke(value: String) = Pragma(value)
    }
}

class ProxyAuthorization internal constructor(override val value: String) : Header() {

    override val name = ProxyAuthorization.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(ProxyAuthorization.Companion)
        }

        override val name = "Proxy-Authorization"
        override operator fun invoke(value: String) = ProxyAuthorization(value)
    }
}

class PublicKeyPins internal constructor(override val value: String) : Header() {

    override val name = PublicKeyPins.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(PublicKeyPins.Companion)
        }

        override val name = "Public-Key-Pins"
        override operator fun invoke(value: String) = PublicKeyPins(value)
    }
}

class Range internal constructor(val unit: RangeUnit, val range: IntRange) : Header() {

    override val name = Range.Companion.name

    override val value by lazy {
        "$unit=${range.first}-${range.last}"
    }

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(Range.Companion)
        }

        override val name = "Range"
        // TODO: Range
        override operator fun invoke(value: String) = throw NotImplementedError("Range header haven't been implemented")
    }
}

class Referer internal constructor(override val value: String) : Header() {

    override val name = Referer.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(Referer.Companion)
        }

        override val name = "Referer"
        override operator fun invoke(value: String) = Referer(value)
    }
}

class Refresh internal constructor(override val value: String) : Header() {

    override val name = Refresh.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(Refresh.Companion)
        }

        override val name = "Refresh"
        override operator fun invoke(value: String) = Refresh(value)
    }
}

class RetryAfter internal constructor(override val value: String) : Header() {

    override val name = RetryAfter.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(RetryAfter.Companion)
        }

        override val name = "Retry-After"
        override operator fun invoke(value: String) = RetryAfter(value)
    }
}

class Server internal constructor(override val value: String) : Header() {

    override val name = Server.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(Server.Companion)
        }

        override val name = "Server"
        override operator fun invoke(value: String) = Server(value)
    }
}

class Status internal constructor(override val value: String) : Header() {

    override val name = Status.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(Status.Companion)
        }

        override val name = "Status"
        override operator fun invoke(value: String) = Status(value)
    }
}

class StrictTransportSecurity internal constructor(override val value: String) : Header() {

    override val name = StrictTransportSecurity.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(StrictTransportSecurity.Companion)
        }

        override val name = "Strict-Transport-Security"
        override operator fun invoke(value: String) = StrictTransportSecurity(value)
    }
}

class Trailer internal constructor(override val value: String) : Header() {

    override val name = Trailer.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(Trailer.Companion)
        }

        override val name = "Trailer"
        override operator fun invoke(value: String) = Trailer(value)
    }
}

class TransferEncoding internal constructor(override val value: String) : Header() {

    override val name = TransferEncoding.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(TransferEncoding.Companion)
        }

        override val name = "Transfer-Encoding"
        override operator fun invoke(value: String) = TransferEncoding(value)
    }
}

class TransferEncodings internal constructor(override val value: String) : Header() {

    override val name = TransferEncodings.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(TransferEncodings.Companion)
        }

        override val name = "TE"
        override operator fun invoke(value: String) = TransferEncodings(value)
    }
}

class Upgrade internal constructor(override val value: String) : Header() {

    override val name = Upgrade.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(Upgrade.Companion)
        }

        override val name = "Upgrade"
        override operator fun invoke(value: String) = Upgrade(value)
    }
}

class UpgradeInsecureRequests internal constructor(override val value: String) : Header() {

    override val name = UpgradeInsecureRequests.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(UpgradeInsecureRequests.Companion)
        }

        override val name = "Upgrade-Insecure-Requests"
        override operator fun invoke(value: String) = UpgradeInsecureRequests(value)
    }
}

class UserAgent internal constructor(override val value: String) : Header() {

    override val name = UserAgent.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(UserAgent.Companion)
        }

        override val name = "User-Agent"
        override operator fun invoke(value: String) = UserAgent(value)
    }
}

class Warning internal constructor(override val value: String) : Header() {

    override val name = Warning.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(Warning.Companion)
        }

        override val name = "Warning"
        override operator fun invoke(value: String) = Warning(value)
    }
}

class WWWAuthenticate internal constructor(override val value: String) : Header() {

    override val name = WWWAuthenticate.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(WWWAuthenticate.Companion)
        }

        override val name = "WWW-Authenticate"
        override operator fun invoke(value: String) = WWWAuthenticate(value)
    }
}
