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

import java.util.Locale

import io.bitsnap.http.headers._
import org.scalatest.{FlatSpec, Inspectors, Matchers, OptionValues}

import scala.util.Random

object HeadersSuite {

  private def qts[T](map: Map[T, Int]) =
    map.map { (e) =>
      s"${e._1.toString};q=0.${e._2}"
    }.mkString("", ", ", "")

  private def listToQ[T](lst: List[T]) =
    qts(lst.map { e =>
      e -> (Random.nextInt(8) + 1)
    }.toMap)

  import CacheControl.Directive._

  val testDate = Header.Date.format(new java.util.Date())

  val testTag = "\\W\"tag\""

  val testHeaders = Map(
    Accept.name                        -> listToQ(List("application/xml", "application/json", "image/png", "video/mpg")),
    AcceptCharset.name                 -> listToQ(Charset.known),
    AcceptDatetime.name                -> testDate,
    AcceptEncoding.name                -> listToQ(Encoding.known),
    AcceptLanguage.name                -> listToQ(List(new Locale("en", "US"), new Locale("en"), new Locale("ru"))),
    AcceptPatch.name                   -> listToQ(List("application/xml", "application/json")),
    AcceptRanges.name                  -> List(Range.Unit.Bytes, Range.Unit.None, new Range.Unit.Specific("pages")).mkString(", "),
    TransferEncodings.name             -> List(Encoding.Br, Encoding.Xpress, Encoding.Gzip).mkString(", "),
    AccessControlAllowCredentials.name -> "true",
    AccessControlAllowCredentials.name -> "false",
    AccessControlAllowHeaders.name     -> headers.known.keys.take(3).mkString(", "),
    AccessControlAllowMethods.name     -> Method.known.take(3).mkString(", "),
    AccessControlAllowOrigin.name      -> "some.site",
    AccessControlExposeHeaders.name    -> headers.known.keys.take(3).mkString(", "),
    AccessControlMaxAge.name           -> "12",
    AccessControlRequestHeaders.name   -> headers.known.keys.take(3).mkString(", "),
    AccessControlRequestMethod.name    -> Method.known.head.toString,
    Allow.name                         -> Method.known.take(3).mkString(", "),
    CacheControl.name -> s"${List(
      NoCache,
      NoCache("abc"),
      Private,
      Private("abc"),
      MaxAge(10),
      SMaxAge(10),
      MaxStale(10),
      MinFresh(10),
      Public,
      NoStore,
      NoTransform,
      OnlyIfCached,
      MustRevalidate,
      ProxyRevalidate
    ).mkString(", ")}, smthng=custom",
    Connection.name         -> "keep-alive",
    ContentDisposition.name -> ContentDisposition.attachment("bla.txt").value,
    ContentEncoding.name    -> Encoding.known.head.toString,
    ContentLanguage.name    -> new Locale("en", "US").toString,
    ContentLength.name      -> "123",
    ContentLocation.name    -> "some.site",
    ContentMD5.name         -> "123456789012345678901234567890AB",
    ContentRange.name       -> new Range.Bounded(1 to 10, Range.Unit.Bytes, 123).toString,
    ContentType.name        -> MimeType("application/xml").toString,
    Cookie.name -> Map("test1" -> "1", "test2" -> "2", "test3" -> "3").map { e =>
      s"${e._1}=${e._2}"
    }.mkString("; "),
    Date.name       -> testDate,
    DoNotTrack.name -> "true",
    Etag.name       -> testTag,
    Expect.name     -> "200-ok",
    Expires.name    -> testDate,
    Forwarded.name -> Seq("for=\"[2001:db8:cafe::17]:4711\"", "proto=http", "by=\"203.0.113.43\"", "host=\"whoknows\"")
      .mkString("; "),
    IfMatch.name           -> testTag,
    IfModifiedSince.name   -> testDate,
    IfNoneMatch.name       -> testTag,
    IfRange.name           -> testDate,
    IfRange.name           -> testTag,
    IfUnmodifiedSince.name -> testDate,
    LastModified.name      -> testDate,
    Link.name              -> "asdf;rel=stylesheet, ad;rel=next",
    MaxForwards.name       -> "100",
    PublicKeyPins.name -> Seq("pin-sha256=\"1\"",
                              "pin-sha256=\"2\"",
                              "max-age=15768000",
                              "includeSubDomains",
                              "report-uri=\"test.com\"").mkString("; "),
    headers.Range.name -> "bytes 100-1/*",
    headers.Range.name -> "bytes 1-100/200",
    RetryAfter.name    -> testDate,
    RetryAfter.name    -> "120",
    SetCookie.name -> Seq("testCookie=testValue",
                          "Path=/test",
                          "Domain=test.com",
                          "Expires=Wed, 9 Jun 2021 10:18:14 GMT",
                          "Max-Age=60",
                          "Secure",
                          "HttpOnly").mkString("; "),
    TrackingStatus.name   -> "!",
    TrackingStatus.name   -> "G",
    TransferEncoding.name -> Encoding.Deflate.toString,
    Vary.name             -> "Retry-After",
    Vary.name             -> "*",
    XContentDuration.name -> "86.6",
    XFrameOptions.name    -> "ALLOW-FROM test.com",
    XSSProtection.name    -> "1; mode=block"
  )
}

class HeadersSuite extends FlatSpec with Matchers with OptionValues with Inspectors {

  "Headers" should "be serializable" in {
    forAll(HeadersSuite.testHeaders) { e =>
      val (name, value) = e
      val apply         = headers.known.get(name).value
      val header        = apply(value)

      header.isSuccess should be(true)
      header.get.value should equal(value)
    }
  }

  they should "be registered" in {
    http.headers.known.keys should equal(HeadersSuite.testHeaders.keys)
  }

  they should "be invalidated if empty" in {
    forAll(headers.known) {
      _._2("").isFailure should be(true)
    }
  }

  they should "have lowercase names" in {
    forAll(HeadersSuite.testHeaders) { e =>
      val (name, _) = e
      name should equal(name.toLowerCase)
    }
  }
}
