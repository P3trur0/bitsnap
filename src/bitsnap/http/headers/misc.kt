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
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import bitsnap.http.Status as HttpStatus
import java.util.Date as JavaDate

class Authorization internal constructor(override val value: String) : Header() {

    override val name = Authorization.Companion.name

    companion object : HeaderCompanion {
        override val name = "Authorization"
        override fun from(value: String) = Authorization(value)
    }
}

class Connection internal constructor(override val value: String) : Header() {

    override val name = Connection.Companion.name

    companion object : HeaderCompanion {
        override val name = "Connection"
        override fun from(value: String) = Connection(value)
    }
}

class Date internal constructor(val date: JavaDate) : Header() {

    override val name = Date.Companion.name

    override val value: String by lazy {
        dateFormat.format(date.toInstant().atZone(ZoneId.of("GMT")))
    }

    companion object : HeaderCompanion {

        private val dateFormat = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneId.of("GMT"))

        override val name = "Date"

        override fun from(value: String) = Date(JavaDate.from(ZonedDateTime.parse(value, dateFormat).toInstant()))
    }
}

class Expect internal constructor(val status: HttpStatus) : Header() {

    override val name = Expect.Companion.name

    override val value by lazy {
        "${status.value()}-${status.toString().toLowerCase()}"
    }

    companion object : HeaderCompanion {
        override val name = "Expect"
        override fun from(value: String) = Expect(HttpStatus.from(value))
    }
}

class From internal constructor(override val value: String) : Header() {

    override val name = From.Companion.name

    companion object : HeaderCompanion {
        override val name = "From"
        override fun from(value: String) = From(value)
    }
}

class Host internal constructor(override val value: String) : Header() {

    override val name = Host.Companion.name

    companion object : HeaderCompanion {
        override val name = "Host"
        override fun from(value: String) = Host(value)
    }
}

class LastModified internal constructor(override val value: String) : Header() {

    override val name = LastModified.Companion.name

    companion object : HeaderCompanion {
        override val name = "Last-Modified"
        override fun from(value: String) = LastModified(value)
    }
}

class Link internal constructor(override val value: String) : Header() {

    override val name = Link.Companion.name

    companion object : HeaderCompanion {
        override val name = "Link"
        override fun from(value: String) = Link(value)
    }
}

class Location internal constructor(override val value: String) : Header() {

    override val name = Location.Companion.name

    companion object : HeaderCompanion {
        override val name = "Location"
        override fun from(value: String) = Location(value)
    }
}

class Origin internal constructor(override val value: String) : Header() {

    override val name = Origin.Companion.name

    companion object : HeaderCompanion {
        override val name = "Origin"
        override fun from(value: String) = Origin(value)
    }
}

class P3P internal constructor(override val value: String) : Header() {

    override val name = P3P.Companion.name

    companion object : HeaderCompanion {
        override val name = "P3P"
        override fun from(value: String) = P3P(value)
    }
}

class Permanent internal constructor(override val value: String) : Header() {

    override val name = Permanent.Companion.name

    companion object : HeaderCompanion {
        override val name = "Permanent"
        override fun from(value: String) = Permanent(value)
    }
}

class Pragma internal constructor(override val value: String) : Header() {

    override val name = Pragma.Companion.name

    companion object : HeaderCompanion {
        override val name = "Pragma"
        override fun from(value: String) = Pragma(value)
    }
}

class ProxyAuthorization internal constructor(override val value: String) : Header() {

    override val name = ProxyAuthorization.Companion.name

    companion object : HeaderCompanion {
        override val name = "Proxy-Authorization"
        override fun from(value: String) = ProxyAuthorization(value)
    }
}

class PublicKeyPins internal constructor(override val value: String) : Header() {

    override val name = PublicKeyPins.Companion.name

    companion object : HeaderCompanion {
        override val name = "Public-Key-Pins"
        override fun from(value: String) = PublicKeyPins(value)
    }
}

class Range internal constructor(val type: AcceptRanges.RangeUnit, val range: IntRange) : Header() {

    override val name = Range.Companion.name

    override val value by lazy {
        "$type=${range.first}-${range.last}"
    }

    companion object : HeaderCompanion {
        override val name = "Range"
        // TODO: Range
        override fun from(value: String) = throw NotImplementedError("Range header haven't been implemented")
    }
}

class Referer internal constructor(override val value: String) : Header() {

    override val name = Referer.Companion.name

    companion object : HeaderCompanion {
        override val name = "Referer"
        override fun from(value: String) = Referer(value)
    }
}

class Refresh internal constructor(override val value: String) : Header() {

    override val name = Refresh.Companion.name

    companion object : HeaderCompanion {
        override val name = "Refresh"
        override fun from(value: String) = Refresh(value)
    }
}

class RetryAfter internal constructor(override val value: String) : Header() {

    override val name = RetryAfter.Companion.name

    companion object : HeaderCompanion {
        override val name = "Retry-After"
        override fun from(value: String) = RetryAfter(value)
    }
}

class Server internal constructor(override val value: String) : Header() {

    override val name = Server.Companion.name

    companion object : HeaderCompanion {
        override val name = "Server"
        override fun from(value: String) = Server(value)
    }
}

class Status internal constructor(override val value: String) : Header() {

    override val name = Status.Companion.name

    companion object : HeaderCompanion {
        override val name = "Status"
        override fun from(value: String) = Status(value)
    }
}

class StrictTransportSecurity internal constructor(override val value: String) : Header() {

    override val name = StrictTransportSecurity.Companion.name

    companion object : HeaderCompanion {
        override val name = "Strict-Transport-Security"
        override fun from(value: String) = StrictTransportSecurity(value)
    }
}

class Trailer internal constructor(override val value: String) : Header() {

    override val name = Trailer.Companion.name

    companion object : HeaderCompanion {
        override val name = "Trailer"
        override fun from(value: String) = Trailer(value)
    }
}

class TransferEncoding internal constructor(override val value: String) : Header() {

    override val name = TransferEncoding.Companion.name

    companion object : HeaderCompanion {
        override val name = "Transfer-Encoding"
        override fun from(value: String) = TransferEncoding(value)
    }
}

class TransferEncodings internal constructor(override val value: String) : Header() {

    override val name = TransferEncodings.Companion.name

    companion object : HeaderCompanion {
        override val name = "TE"
        override fun from(value: String) = TransferEncodings(value)
    }
}

class Upgrade internal constructor(override val value: String) : Header() {

    override val name = Upgrade.Companion.name

    companion object : HeaderCompanion {
        override val name = "Upgrade"
        override fun from(value: String) = Upgrade(value)
    }
}

class UpgradeInsecureRequests internal constructor(override val value: String) : Header() {

    override val name = UpgradeInsecureRequests.Companion.name

    companion object : HeaderCompanion {
        override val name = "Upgrade-Insecure-Requests"
        override fun from(value: String) = UpgradeInsecureRequests(value)
    }
}

class UserAgent internal constructor(override val value: String) : Header() {

    override val name = UserAgent.Companion.name

    companion object : HeaderCompanion {
        override val name = "User-Agent"
        override fun from(value: String) = UserAgent(value)
    }
}

class Warning internal constructor(override val value: String) : Header() {

    override val name = Warning.Companion.name

    companion object : HeaderCompanion {
        override val name = "Warning"
        override fun from(value: String) = Warning(value)
    }
}

class WWWAuthenticate internal constructor(override val value: String) : Header() {

    override val name = WWWAuthenticate.Companion.name

    companion object : HeaderCompanion {
        override val name = "WWW-Authenticate"
        override fun from(value: String) = WWWAuthenticate(value)
    }
}
