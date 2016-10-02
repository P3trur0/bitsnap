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
package headers

import io.bitsnap.http.Header.Implicit.{Date => HeaderDate}
import io.bitsnap.util._

final class SetCookie(val cookieName: String,
                      val cookieValue: String,
                      val path: Option[String],
                      val domain: Option[String],
                      val expires: Option[HeaderDate],
                      val maxAge: Option[Int],
                      val secure: Boolean,
                      val httpOnly: Boolean)
    extends Header {

  if (cookieName.trim.isEmpty || (maxAge.isDefined && maxAge.get <= 0)) {
    throw SetCookie.Invalid
  }

  override val name = SetCookie.name

  override lazy val value = {
    val builder = StringBuilder.newBuilder

    builder ++= s"$cookieName=$cookieValue"
    if (path.isDefined) { builder ++= s"; Path=${path.get}" }
    if (domain.isDefined) { builder ++= s"; Domain=${domain.get}" }
    if (expires.isDefined) { builder ++= s"; Expires=${expires.get}" }
    if (maxAge.isDefined) { builder ++= s"; Max-Age=${maxAge.get}" }
    if (secure) { builder ++= "; Secure" }
    if (httpOnly) { builder ++= "; HttpOnly" }

    builder.toString
  }

  override def equals(that: Any) = that match {
    case that: SetCookie =>
      this.cookieName == that.cookieName &&
        this.cookieValue == that.cookieValue &&
        this.path == that.path &&
        this.domain == that.domain &&
        this.expires == that.expires &&
        this.maxAge == that.maxAge &&
        this.secure == that.secure &&
        this.httpOnly == that.httpOnly
    case _ => false
  }

  override def hashCode =
    headers.hashCodePrime +
      cookieName.hashCode +
      cookieValue.hashCode +
      path.hashCode +
      domain.hashCode +
      expires.hashCode +
      maxAge.hashCode +
      secure.hashCode +
      httpOnly.hashCode
}

object SetCookie {
  private[http] final val name = "set-cookie"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String) = {
    if (string.isEmpty) { throw Invalid }

    var name                        = ""
    var value                       = ""
    var path: Option[String]        = None
    var domain: Option[String]      = None
    var expires: Option[HeaderDate] = None
    var maxAge: Option[Int]         = None
    var secure                      = false
    var httpOnly                    = false

    string.split(";").map { _.trim }.foreach { e =>
      if (name.isEmpty) {
        val (n, v) = e.splitNameValue.getOrElse { throw Invalid }
        name = n
        value = v
      } else {
        try {
          val lw = e.toLowerCase
          e match {
            case _ if lw.startsWith("path=") =>
              path = Some(
                e.substringAfter('=')
                  .getOrElse {
                    throw Invalid
                  }
                  .stripQuotes)

            case _ if lw.startsWith("domain=") =>
              domain = Some(
                e.substringAfter('=')
                  .getOrElse {
                    throw Invalid
                  }
                  .stripQuotes)

            case _ if lw.startsWith("expires=") =>
              val HeaderDate(d) = e.substringAfter('=').getOrElse { throw Invalid }
              expires = Some(d)

            case _ if lw.startsWith("max-age=") =>
              maxAge = Some(
                e.substringAfter('=')
                  .getOrElse {
                    throw Invalid
                  }
                  .toInt)

            case _ if lw.startsWith("secure")   => secure = true
            case _ if lw.startsWith("httponly") => httpOnly = true
          }
        } catch {
          case _: NumberFormatException => throw Invalid
        }
      }
    }

    new SetCookie(name, value, path, domain, expires, maxAge, secure, httpOnly)
  }
}
