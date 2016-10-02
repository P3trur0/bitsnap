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
package io.bitsnap
package http

private[http] sealed abstract class Status(val value: Int) extends Status.Checks {

  lazy val name = getClass.getSimpleName.toLowerCase.stripSuffix("$")

  val description: String

  override def toString = s"$value $description"

  override def equals(other: Any) = other match {
    case that: Status => value == that.value
    case _            => false
  }

  override def hashCode = http.hashCodePrime + value.hashCode
}

final class UnknownStatus private[http] (override val value: Int) extends Status(value) {
  override val description = "Unknown Status"
}

object Status {

  private[http] object Type {

    private[http] final val success = Seq(
      OK,
      Created,
      Accepted,
      NonAuthoritativeInformation,
      NoContent,
      ResetContent,
      PartialContent,
      MultiStatus,
      AlreadyReported,
      IMUsed
    )

    private[http] final val redirection = Seq(
      MultipleChoices,
      MovedPermanently,
      Found,
      SeeOther,
      NotModified,
      UseProxy,
      Unused,
      TemporaryRedirect,
      PermanentRedirect
    )

    private[http] final val error = Seq(
      BadRequest,
      Unauthorized,
      PaymentRequired,
      Forbidden,
      NotFound,
      MethodNotAllowed,
      NotAcceptable,
      ProxyAuthenticationRequired,
      RequestTimeout,
      Conflict,
      Gone,
      LengthRequired,
      PreconditionFailed,
      RequestEntityTooLarge,
      RequestURITooLong,
      UnsupportedMediaType,
      RequestRangeNotSatisfiable,
      ExpectationFailed,
      ImATeapot,
      EnhanceYourCalm,
      UnprocessableEntity,
      Locked,
      FailedDependency,
      WebDAVReserved,
      UpgradeRequired,
      PreconditionRequired,
      TooManyRequests,
      RequestHeaderFieldsTooLarge,
      NoResponse,
      RetryWith,
      BlockedByWindowsParentalControls,
      UnavailableForLegalReasons,
      ClientClosedRequest
    )

    private[http] final val serverError = Seq(
      InternalServerError,
      NotImplemented,
      BadGateway,
      ServiceUnavailable,
      GatewayTimeout,
      HTTPVersionNotSupported,
      VariantAlsoNegotiates,
      InsufficientStorage,
      LoopDetected,
      BandwidthLimitExceeded,
      NotExtended,
      NetworkAuthenticationRequired,
      ReadTimeoutError,
      ConnectTimeoutError
    )

    sealed trait Success

    sealed trait Redirection

    sealed trait Error

    sealed trait ServerError extends Error
  }

  private[http] trait Checks { this: Status =>
    final def isSuccess     = this.isInstanceOf[Status.Type.Success]
    final def isRedirection = this.isInstanceOf[Status.Type.Redirection]
    final def isError       = this.isInstanceOf[Status.Type.Error]
    final def isServerError = this.isInstanceOf[Status.Type.ServerError]
    final def isUnknown     = this.isInstanceOf[UnknownStatus]
  }

  def apply(value: Int) = {
    (value / 100 match {
      case 2 if value <= 226 => Status.Type.success
      case 3 if value <= 308 => Status.Type.redirection
      case 4                 => Status.Type.error
      case 5                 => Status.Type.serverError
    }) find { _.value == value } getOrElse { new UnknownStatus(value) }
  }

  def apply(name: String) = {
    val lc             = name.toLowerCase()
    var found: Boolean = false

    Seq(
      Type.success,
      Type.redirection,
      Type.error,
      Type.serverError
    ).map { c =>
      if (!found) {
        val e = c.find { _.name == lc }
        if (e.isDefined) {
          found = true
          Some(e)
        } else { None }
      } else { None }
    }.filter { _.isDefined }.map { _.get }.head
  }

  object OK extends Status(200) with Status.Type.Success {
    override val description = "OK"
  }

  object Created extends Status(201) with Status.Type.Success {
    override val description = "Created"
  }

  object Accepted extends Status(202) with Status.Type.Success {
    override val description = "Accepted"
  }

  object NonAuthoritativeInformation extends Status(203) with Status.Type.Success {
    override val description = "Non-Authoritative Information"
  }

  object NoContent extends Status(204) with Status.Type.Success {
    override val description = "No content"
  }

  object ResetContent extends Status(205) with Status.Type.Success {
    override val description = "Reset content"
  }

  object PartialContent extends Status(206) with Status.Type.Success {
    override val description = "Partial content"
  }

  object MultiStatus extends Status(207) with Status.Type.Success {
    override val description = "Multi-status (WebDAV)"
  }

  object AlreadyReported extends Status(208) with Status.Type.Success {
    override val description = "Already reported (WebDAV)"
  }

  object IMUsed extends Status(226) with Status.Type.Success {
    override val description = "IM Used"
  }

  /* redirection */

  object MultipleChoices extends Status(300) with Status.Type.Redirection {
    override val description = "Multiple Choices"
  }

  object MovedPermanently extends Status(301) with Status.Type.Redirection {
    override val description = "Moved Permanently"
  }

  object Found extends Status(302) with Status.Type.Redirection {
    override val description = "Found"
  }

  object SeeOther extends Status(303) with Status.Type.Redirection {
    override val description = "See Other"
  }

  object NotModified extends Status(304) with Status.Type.Redirection {
    override val description = "Not Modified"
  }

  object UseProxy extends Status(305) with Status.Type.Redirection {
    override val description = "Use Proxy"
  }

  object Unused extends Status(306) with Status.Type.Redirection {
    override val description = "Unused Redirection Status"
  }

  object TemporaryRedirect extends Status(307) with Status.Type.Redirection {
    override val description = "Temporary redirect"
  }

  object PermanentRedirect extends Status(308) with Status.Type.Redirection {
    override val description = "Permanent redirect"
  }

  /* error */

  object BadRequest extends Status(400) with Status.Type.Error {
    override val description = "Bad Request"
  }

  object Unauthorized extends Status(401) with Status.Type.Error {
    override val description = "Unauthorized"
  }

  object PaymentRequired extends Status(402) with Status.Type.Error {
    override val description = "Payment Required"
  }

  object Forbidden extends Status(403) with Status.Type.Error {
    override val description = "Forbidden"
  }

  object NotFound extends Status(404) with Status.Type.Error {
    override val description = "Not found"
  }

  object MethodNotAllowed extends Status(405) with Status.Type.Error {
    override val description = "Method not allowed"
  }

  object NotAcceptable extends Status(406) with Status.Type.Error {
    override val description = "Not acceptable"
  }

  object ProxyAuthenticationRequired extends Status(407) {
    override val description = "Proxy authentication required"
  }

  object RequestTimeout extends Status(408) with Status.Type.Error {
    override val description = "Request timeout"
  }

  object Conflict extends Status(409) with Status.Type.Error {
    override val description = "Conflict"
  }

  object Gone extends Status(410) with Status.Type.Error {
    override val description = "Gone"
  }

  object LengthRequired extends Status(411) with Status.Type.Error {
    override val description = "Length required"
  }

  object PreconditionFailed extends Status(412) with Status.Type.Error {
    override val description = "Precondition failed"
  }

  object RequestEntityTooLarge extends Status(413) with Status.Type.Error {
    override val description = "Request entity too large"
  }

  object RequestURITooLong extends Status(414) with Status.Type.Error {
    override val description = "Request URI too long"
  }

  object UnsupportedMediaType extends Status(415) with Status.Type.Error {
    override val description = "Unsupported media type"
  }

  object RequestRangeNotSatisfiable extends Status(416) with Status.Type.Error {
    override val description = "Request range not satisfiable"
  }

  object ExpectationFailed extends Status(417) with Status.Type.Error {
    override val description = "Expectation failed"
  }

  object ImATeapot extends Status(418) with Status.Type.Error {
    override val description = "I'm a teapot"
  }

  object EnhanceYourCalm extends Status(420) with Status.Type.Error {
    override val description = "Enhance your calm"
  }

  object UnprocessableEntity extends Status(422) with Status.Type.Error {
    override val description = "Unprocessable Entity (WebDAV)"
  }

  object Locked extends Status(423) with Status.Type.Error {
    override val description = "Locked (WebDAV)"
  }

  object FailedDependency extends Status(424) with Status.Type.Error {
    override val description = "Failed Dependency (WebDAV)"
  }

  object WebDAVReserved extends Status(425) with Status.Type.Error {
    override val description = "Reserved for WebDAV"
  }

  object UpgradeRequired extends Status(426) with Status.Type.Error {
    override val description = "Upgrade Required"
  }

  object PreconditionRequired extends Status(428) with Status.Type.Error {
    override val description = "Precondition Required"
  }

  object TooManyRequests extends Status(429) with Status.Type.Error {
    override val description = "Too Many Requests"
  }

  object RequestHeaderFieldsTooLarge extends Status(431) with Status.Type.Error {
    override val description = "Request Header Fields Too Large"
  }

  object NoResponse extends Status(444) with Status.Type.Error {
    override val description = "No Response (Nginx)"
  }

  object RetryWith extends Status(449) with Status.Type.Error {
    override val description = "Retry With (Microsoft)"
  }

  object BlockedByWindowsParentalControls extends Status(450) with Status.Type.Error {
    override val description =
      "Blocked by Windows Parental Controls (Microsoft)"
  }

  object UnavailableForLegalReasons extends Status(451) with Status.Type.Error {
    override val description = "Unavailable For Legal Reasons"
  }

  object ClientClosedRequest extends Status(499) with Status.Type.Error {
    override val description = "Client Closed Request (Nginx)"
  }

  /* server error */

  object InternalServerError extends Status(500) with Status.Type.ServerError {
    override val description = "Internal Server Error"
  }

  object NotImplemented extends Status(501) with Status.Type.ServerError {
    override val description = "Not Implemented"
  }

  object BadGateway extends Status(502) with Status.Type.ServerError {
    override val description = "Bad Gateway"
  }

  object ServiceUnavailable extends Status(503) with Status.Type.ServerError {
    override val description = "Service Unavailable"
  }

  object GatewayTimeout extends Status(504) with Status.Type.ServerError {
    override val description = "Gateway Timeout"
  }

  object HTTPVersionNotSupported extends Status(505) with Status.Type.ServerError {
    override val description = "HTTP Version Not Supported"
  }

  object VariantAlsoNegotiates extends Status(506) with Status.Type.ServerError {
    override val description = "Variant Also Negotiates (Experimental)"
  }

  object InsufficientStorage extends Status(507) with Status.Type.ServerError {
    override val description = "Insufficient Storage (WebDAV)"
  }

  object LoopDetected extends Status(508) with Status.Type.ServerError {
    override val description = "Loop Detected (WebDAV)"
  }

  object BandwidthLimitExceeded extends Status(509) with Status.Type.ServerError {
    override val description = "Bandwidth Limit Exceeded (Apache)"
  }

  object NotExtended extends Status(510) with Status.Type.ServerError {
    override val description = "Not Extended"
  }

  object NetworkAuthenticationRequired extends Status(511) with Status.Type.ServerError {
    override val description = "Authentication Required"
  }

  object ReadTimeoutError extends Status(598) with Status.Type.ServerError {
    override val description = "Read timeout"
  }

  object ConnectTimeoutError extends Status(599) with Status.Type.ServerError {
    override val description = "Connection timeout"
  }
}
