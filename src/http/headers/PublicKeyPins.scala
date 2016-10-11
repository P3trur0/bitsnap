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

import io.bitsnap.util._

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

final class PublicKeyPins(val publicSha256: Seq[String],
                          val includeSubDomains: Boolean,
                          val maxAge: Int,
                          val reportUri: Option[String])
    extends Header {

  if (maxAge <= 0) {
    throw PublicKeyPins.Invalid
  }

  override val name = PublicKeyPins.name

  override lazy val value = {
    val builder = mutable.StringBuilder.newBuilder

    builder ++= publicSha256.map {
      "pin-sha256=\"".concat(_).concat("\"")
    }.mkString("; ")

    builder ++= s"; max-age=$maxAge"

    if (includeSubDomains) {
      builder ++= "; includeSubDomains"
    }

    if (reportUri.isDefined) {
      builder ++= "; report-uri=\""
      builder ++= reportUri.get
      builder ++= "\""
    }

    builder.toString()
  }
}

object PublicKeyPins {

  private[http] final val name = "public-key-pins"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String): Try[PublicKeyPins] = {
    if (string.isEmpty) {
      Failure(Invalid)
    } else {

      val publicSha256      = new mutable.MutableList[String]
      var includeSubDomains = false
      var maxAge            = 0
      var reportUri         = ""

      var error = false

      string.split(";").map { _.trim }.foreach { e =>
        val lw = e.toLowerCase.trim
        e match {
          case _ if lw.startsWith("pin-sha256=") =>
            publicSha256 += e.substringAfter('=').getOrElse { error = true; "" }.stripQuotes
          case _ if lw.startsWith("max-age=") =>
            Try(e.substringAfter('=').getOrElse { "" }.toInt) match {
              case Success(e) => maxAge = e
              case Failure(_) => error = true
            }
          case _ if lw.startsWith("includesubdomains") => includeSubDomains = true
          case _ if lw.startsWith("report-uri=") =>
            reportUri = e.substringAfter('=').getOrElse { error = true; "" }.stripQuotes
        }
      }

      if (!error) {
        Success(new PublicKeyPins(publicSha256, includeSubDomains, maxAge, if (reportUri.isEmpty) {
          None
        } else {
          Some(reportUri)
        }))
      } else {
        Failure(Invalid)
      }
    }
  }
}
