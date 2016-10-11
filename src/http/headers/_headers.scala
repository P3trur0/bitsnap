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
package io.bitsnap.http

import scala.util.Try

package object headers {
  private[headers] val hashCodePrime = 31

  private[http] val known = Map[String, (String) => Try[Header]](
    Accept.name                        -> Accept.apply,
    AcceptCharset.name                 -> AcceptCharset.apply,
    AcceptDatetime.name                -> AcceptDatetime.apply,
    AcceptEncoding.name                -> AcceptEncoding.apply,
    AcceptLanguage.name                -> AcceptLanguage.apply,
    AcceptPatch.name                   -> AcceptPatch.apply,
    AcceptRanges.name                  -> AcceptRanges.apply,
    TransferEncodings.name             -> TransferEncodings.apply,
    AccessControlAllowCredentials.name -> AccessControlAllowCredentials.apply,
    AccessControlAllowHeaders.name     -> AccessControlAllowHeaders.apply,
    AccessControlAllowMethods.name     -> AccessControlAllowMethods.apply,
    AccessControlAllowOrigin.name      -> AccessControlAllowOrigin.apply,
    AccessControlExposeHeaders.name    -> AccessControlExposeHeaders.apply,
    AccessControlMaxAge.name           -> AccessControlMaxAge.apply,
    AccessControlRequestHeaders.name   -> AccessControlRequestHeaders.apply,
    AccessControlRequestMethod.name    -> AccessControlRequestMethod.apply,
    Allow.name                         -> Allow.apply,
    CacheControl.name                  -> CacheControl.apply,
    Connection.name                    -> Connection.apply,
    ContentDisposition.name            -> ContentDisposition.apply,
    ContentEncoding.name               -> ContentEncoding.apply,
    ContentLanguage.name               -> ContentLanguage.apply,
    ContentLength.name                 -> ContentLength.apply,
    ContentLocation.name               -> ContentLocation.apply,
    ContentMD5.name                    -> ContentMD5.apply,
    ContentRange.name                  -> ContentRange.apply,
    ContentType.name                   -> ContentType.apply,
    Cookie.name                        -> Cookie.apply,
    Date.name                          -> Date.apply,
    DoNotTrack.name                    -> DoNotTrack.apply,
    Etag.name                          -> Etag.apply,
    Expect.name                        -> Expect.apply,
    Expires.name                       -> Expires.apply,
    Forwarded.name                     -> Forwarded.apply,
    IfMatch.name                       -> IfMatch.apply,
    IfModifiedSince.name               -> IfModifiedSince.apply,
    IfNoneMatch.name                   -> IfNoneMatch.apply,
    IfRange.name                       -> IfRange.apply,
    IfUnmodifiedSince.name             -> IfUnmodifiedSince.apply,
    LastModified.name                  -> LastModified.apply,
    Link.name                          -> Link.apply,
    MaxForwards.name                   -> MaxForwards.apply,
    PublicKeyPins.name                 -> PublicKeyPins.apply,
    headers.Range.name                 -> headers.Range.apply,
    RetryAfter.name                    -> RetryAfter.apply,
    SetCookie.name                     -> SetCookie.apply,
    TrackingStatus.name                -> TrackingStatus.apply,
    TransferEncoding.name              -> TransferEncoding.apply,
    Vary.name                          -> Vary.apply,
    XContentDuration.name              -> XContentDuration.apply,
    XFrameOptions.name                 -> XFrameOptions.apply,
    XSSProtection.name                 -> XSSProtection.apply
  )
}
