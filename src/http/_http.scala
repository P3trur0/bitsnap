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

import io.bitsnap.http.headers._
import io.bitsnap.util.RichSeq

import scala.language.implicitConversions
import scala.util.{Failure, Success}

package object http {

  private[http] val hashCodePrime = 11

  implicit class Headers(val seq: Seq[Header]) extends AnyVal {
    final def find[T <: Header](name: String) = {
      val n = name.toLowerCase
      if (n == Cookie.name || n == SetCookie.name) {
        throw new RuntimeException("Use Headers.cookies, Headers.setCookies instead")
      }

      Headers.Implicits.toRichSeq(this).binarySearch(n) match {
        case Some(x) => Some(x.asInstanceOf[T])
        case None    => None
      }
    }

    private[http] final def filter[T <: Header](name: String) = {
      val n = name.toLowerCase
      seq.filter { _.name.toLowerCase == n } map {
        _.asInstanceOf[T]
      }
    }

    // Multiple headers

    final def cookies = filter[Cookie](Cookie.name)

    final def setCookies = filter[SetCookie](SetCookie.name)

    final def link = filter[Link](Link.name)

    // Generic

    final def origin = find[Header]("origin")

    final def host = find[Header]("host")

    final def from = find[Header]("from")

    final def referer = find[Header]("referer")

    final def upgrade = find[Header]("upgrade")

    final def upgradeInsecureRequests = find[Header]("upgrade-insecure-requests")

    final def https = find[Header]("https")

    // Accept

    final def accept = find[Accept](Accept.name)

    final def acceptCharset = find[AcceptCharset](AcceptCharset.name)

    final def acceptDatetime = find[AcceptDatetime](AcceptDatetime.name)

    final def acceptEncoding = find[AcceptEncoding](AcceptEncoding.name)

    final def acceptLanguage = find[AcceptLanguage](AcceptLanguage.name)

    final def acceptPatch = find[AcceptPatch](AcceptPatch.name)

    final def acceptRanges = find[AcceptRanges](AcceptRanges.name)

    final def transferEncodings = find[TransferEncodings](TransferEncodings.name)

    // AccessControl

    final def accessControlAllowCredentials =
      find[AccessControlAllowCredentials](AccessControlAllowCredentials.name)

    final def accessControlAllowHeaders = find[AccessControlAllowHeaders](AccessControlAllowHeaders.name)

    final def accessControlAllowMethods = find[AccessControlAllowMethods](AccessControlAllowMethods.name)

    final def accessControlAllowOrigin = find[AccessControlAllowOrigin](AccessControlAllowOrigin.name)

    final def accessControlExposeHeaders = find[AccessControlExposeHeaders](AccessControlExposeHeaders.name)

    final def accessControlMaxAge = find[AccessControlMaxAge](AccessControlMaxAge.name)

    final def accessControlRequestHeaders = find[AccessControlRequestHeaders](AccessControlRequestHeaders.name)

    final def accessControlRequestMethod = find[AccessControlRequestMethod](AccessControlRequestMethod.name)

    // ---

    final def allow = find[Allow](Allow.name)

    final def cacheControl = find[CacheControl](CacheControl.name)

    final def connection = find[Connection](Connection.name)

    // Content

    final def contentDisposition = find[ContentDisposition](ContentDisposition.name)

    final def contentEncoding = find[ContentEncoding](ContentEncoding.name)

    final def contentLanguage = find[ContentLanguage](ContentLanguage.name)

    final def contentLength = find[ContentLength](ContentLength.name)

    final def contentLocation = find[ContentLocation](ContentLocation.name)

    final def contentMD5 = find[ContentMD5](ContentMD5.name)

    final def contentRange = find[ContentRange](ContentRange.name)

    final def contentType = find[ContentType](ContentType.name)

    // CSP

    // ---

    final def date = find[Date](Date.name)

    final def doNotTrack = find[DoNotTrack](DoNotTrack.name)

    final def etag = find[Etag](Etag.name)

    final def expect = find[Expect](Expect.name)

    final def expires = find[Expires](Expires.name)

    final def forwarded = find[Forwarded](Forwarded.name)

    // IF

    final def ifMatch = find[IfMatch](IfMatch.name)

    final def ifModifiedSince = find[IfModifiedSince](IfModifiedSince.name)

    final def ifNoneMatch = find[IfNoneMatch](IfNoneMatch.name)

    final def ifRange = find[IfRange](IfRange.name)

    final def ifUnmodifiedSince = find[IfUnmodifiedSince](IfUnmodifiedSince.name)

    // ---

    final def lastModified = find[LastModified](LastModified.name)

    // there might be multiple link headers

    final def maxForwards = find[MaxForwards](MaxForwards.name)

    final def publicKeyPins = find[PublicKeyPins](PublicKeyPins.name)

    final def range = find[headers.Range](headers.Range.name)

    final def retryAfter = find[RetryAfter](RetryAfter.name)

    final def setCookie = find[SetCookie](SetCookie.name)

    final def trackingStatus = find[TrackingStatus](TrackingStatus.name)

    final def transferEncoding = find[TransferEncoding](TransferEncoding.name)

    final def vary = find[Vary](Vary.name)

    // X

    final def xContentDuration = find[XContentDuration](XContentDuration.name)

    final def xFrameOptions = find[XFrameOptions](XFrameOptions.name)

    final def xXSSProtection = find[XSSProtection](XSSProtection.name)

    // ---
  }

  object Headers {

    def unapply(string: String): Seq[Header] = unapply(string.split("\r\n"))

    def unapply(strings: Seq[String]): Seq[Header] = {
      strings.map { s =>
        val colonIdx = s.indexOf(": ")
        if (colonIdx > 0) {
          val (n, v) = (s.substring(0, colonIdx), s.substring(colonIdx))
          val apply  = headers.known.get(n)
          if (apply.isDefined) {
            apply.get(v) match {
              case Success(header) => Some(header)
              case Failure(e)      => None
            }
          } else {
            Some(new Header {
              override val name: String  = n
              override val value: String = v
            })
          }
        } else {
          None
        }
      }.filter { _.isDefined }.map { _.get }
    }

    private[http] object Implicits {
      implicit def toHeaders(seq: Seq[Header]): Headers =
        Headers(seq.sortWith { (a, b) =>
          a.name > b.name || a.name == b.name && a.value > b.value
        })

      implicit def fromHeaders(headers: Headers): Seq[Header] = headers.seq.seq

      implicit def toRichSeq(headers: Headers): RichSeq[String, Header] = RichSeq[String, Header](headers)

      implicit def fromRichSeq(richSeq: RichSeq[String, Header]): Headers = Headers(richSeq.from)
    }
  }
}
