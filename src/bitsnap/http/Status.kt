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

package bitsnap.http

import bitsnap.exceptions.InvalidStatusException
import bitsnap.exceptions.UnknownStatusException
import java.util.*

/**
 * HTTP 1.1 Status Codes
 */
interface Status {

    fun value(): Int

    enum class Informational(val value: Int, val str: String) : Status {
        Continue(100, "Continue"),
        SwitchingProtocols(101, "Switching Protocols"),
        Processing(102, "Processing");

        override fun toString() = str
        override fun value() = value
    }

    enum class Success(val value: Int, val str: String) : Status {
        OK(200, "OK"),
        Created(201, "Created"),
        Accepted(202, "Accepted"),
        NonAuthoritativeInformation(203, "Non-Authoritative Information"),
        NoContent(204, "No Content"),
        ResetContent(205, "Reset Content"),
        PartialContent(206, "Partial Content"),
        MultiStatus(207, "Multi-Status (WebDAV)"),
        AlreadyReported(208, "Already Reported (WebDAV)"),
        IMUsed(226, "IM Used");

        override fun toString() = str
        override fun value() = value
    }

    enum class Redirection(val value: Int, val str: String) : Status {

        MultipleChoices(300, "Multiple Choices"),
        MovedPermanently(301, "Moved Permanently"),
        Found(302, "Found"),
        SeeOther(303, "See Other"),
        NotModified(304, "Not Modified"),
        UseProxy(305, "Use Proxy"),
        Unused(306, "Unused Redirection Status"),
        TemporaryRedirect(307, "Temporary Redirect"),
        PermanentRedirect(308, "Permanent Redirect");

        override fun toString() = str
        override fun value() = value
    }

    enum class ClientError(val value: Int, val str: String) : Status {
        BadRequest(400, "Bad Request"),
        Unauthorized(401, "Unauthorized"),
        PaymentRequired(402, "Payment Required"),
        Forbidden(403, "Forbidden"),
        NotFound(404, "Not Found"),
        MethodNotAllowed(405, "Method Not Allowed"),
        NotAcceptable(406, "Not Acceptable"),
        ProxyAuthenticationRequired(407, "Proxy Authentication Required"),
        RequestTimeout(408, "Request Timeout"),
        Conflict(409, "Conflict"),
        Gone(410, "Gone"),
        LengthRequired(411, "Length Required"),
        PreconditionFailed(412, "Precondition Failed"),
        RequestEntityTooLarge(413, "Request Entity Too Large"),
        RequestURITooLong(414, "Request-URI Too Long"),
        UnsupportedMediaType(415, "Unsupported Media Type"),
        RequestedRangeNotSatisfiable(416, "Requested Range Not Satisfiable"),
        ExpectationFailed(417, "Expectation Failed"),
        ImATeapot(418, "I'm a teapot (RFC 2324)"),
        EnhanceYourCalm(420, "Enhance Your Calm (Twitter)"),
        UnprocessableEntity(422, "Unprocessable Entity (WebDAV)"),
        Locked(423, "Locked (WebDAV)"),
        FailedDependency(424, "Failed Dependency (WebDAV)"),
        WebDAVReserved(425, "Reserved for WebDAV"),
        UpgradeRequired(426, "Upgrade Required"),
        PreconditionRequired(428, "Precondition Required"),
        TooManyRequests(429, "Too Many Requests"),
        RequestHeaderFieldsTooLarge(431, "Request Header Fields Too Large"),
        NoResponse(444, "No Response (Nginx)"),
        RetryWith(449, "Retry With (Microsoft)"),
        BlockedByWindowsParentalControls(450, "Blocked by Windows Parental Controls (Microsoft)"),
        UnavailableForLegalReasons(451, "Unavailable For Legal Reasons"),
        ClientClosedRequest(499, "Client Closed Request (Nginx)");

        override fun toString() = str
        override fun value() = value
    }

    enum class ServerError(val value: Int, val str: String) : Status {
        InternalServerError(500, "Internal Server Error"),
        NotImplemented(501, "Not Implemented"),
        BadGateway(502, "Bad Gateway"),
        ServiceUnavailable(503, "Service Unavailable"),
        GatewayTimeout(504, "Gateway Timeout"),
        HTTPVersionNotSupported(505, "HTTP Version Not Supported"),
        VariantAlsoNegotiates(506, "Variant Also Negotiates (Experimental)"),
        InsufficientStorage(507, "Insufficient Storage (WebDAV)"),
        LoopDetected(508, "Loop Detected (WebDAV)"),
        BandwidthLimitExceeded(509, "Bandwidth Limit Exceeded (Apache)"),
        NotExtended(510, "Not Extended"),
        NetworkAuthenticationRequired(511, "Network Authentication Required"),
        ReadTimeoutError(598, "Network read timeout error"),
        ConnectTimeoutError(599, "Network connect timeout error");

        override fun toString() = str
        override fun value() = value
    }

    companion object {

        val statuses: Map<Int, Status> by lazy {
            val statusMap: MutableMap<Int, Status> = HashMap()

            Informational.values().associateTo(statusMap) {
                Pair(it.value, it)
            }

            Success.values().associateTo(statusMap) {
                Pair(it.value, it)
            }

            Redirection.values().associateTo(statusMap) {
                Pair(it.value, it)
            }

            ClientError.values().associateTo(statusMap) {
                Pair(it.value, it)
            }

            ServerError.values().associateTo(statusMap) {
                Pair(it.value, it)
            }

            statusMap
        }

        operator fun invoke(value: Int) = statuses[value.toInt()] ?: throw UnknownStatusException(value.toString())

        operator fun invoke(value: String) = try {
            val status = value.toInt()
            if (status <= 0) {
                throw NumberFormatException()
            }

            invoke(status)
        } catch(e: NumberFormatException) {
            throw InvalidStatusException(value)
        }
    }
}
