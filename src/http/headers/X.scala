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

import scala.util.{Failure, Success, Try}

final class XContentDuration(val duration: Float) extends Header {

  override val name = XContentDuration.name

  override lazy val value = duration.toString

  override def equals(that: Any) = that match {
    case that: XContentDuration => this.duration == that.duration
    case _                      => false
  }

  override def hashCode = headers.hashCodePrime + duration.hashCode
}

object XContentDuration {

  private[http] final val name = "x-content-duration"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String): Try[XContentDuration] = {
    val trimmed = string.trim

    if (trimmed.isEmpty) {
      Failure(Invalid)
    } else {
      Try(new XContentDuration(trimmed.toFloat)) match {
        case Failure(e) if e.isInstanceOf[NumberFormatException] => Failure(Invalid)
        case e                                                   => e
      }
    }
  }
}

final class XFrameOptions(val directive: XFrameOptions.Directive) extends Header {

  override val name = XFrameOptions.name

  override lazy val value = directive.toString

  override def equals(that: Any) = that match {
    case that: XFrameOptions => this.directive == that.directive
    case _                   => false
  }

  override def hashCode = headers.hashCodePrime + directive.hashCode
}

object XFrameOptions {

  private[http] final val name = "x-frame-options"

  override def toString = name

  object Invalid extends Header.Invalid

  sealed abstract class Directive(private[http] val name: String, val value: Option[String]) {
    override def toString = s"$name${if (value.isDefined) { s" ${value.get}" } else { "" }}"

    override def equals(that: Any) = that match {
      case that: Directive => this.name == that.name && this.value == that.value
      case _               => false
    }

    override def hashCode = headers.hashCodePrime + name.hashCode + value.hashCode
  }

  object Deny extends Directive("DENY", None)

  object SameOrigin extends Directive("SAMEORIGIN", None)

  class AllowFrom(origin: String) extends Directive("ALLOW-FROM", Some(origin))

  def apply(string: String): Try[XFrameOptions] =
    (string match {
      case _ if string.startsWith(Deny.name)       => Success(Deny)
      case _ if string.startsWith(SameOrigin.name) => Success(SameOrigin)
      case _ if string.startsWith("ALLOW-FROM") =>
        Try(new AllowFrom(string.substringAfter(' ').getOrElse { throw Invalid }))
      case _ => Failure(Invalid)
    }).map { new XFrameOptions(_) }
}

final class XSSProtection(val enabled: Boolean, val blockMode: Boolean) extends Header {

  if (!enabled && blockMode) {
    throw XSSProtection.Invalid
  }

  override val name = XSSProtection.name

  override lazy val value = s"${if (enabled) { "1" } else { "0" }}${if (blockMode) { "; mode=block" } else { "" }}"

  override def equals(that: Any) = that match {
    case that: XSSProtection =>
      this.enabled == that.enabled &&
        this.blockMode == that.blockMode
    case _ => false
  }

  override def hashCode = headers.hashCodePrime + enabled.hashCode + blockMode.hashCode
}

object XSSProtection {

  private[http] final val name = "xss-protection"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String): Try[XSSProtection] = {
    if (string.trim.isEmpty) {
      Failure(Invalid)
    } else {
      Try(new XSSProtection(enabled = string(0) == '1', blockMode = if (string.size > 1) {
        if (string.substringAfter(' ').getOrElse { throw Invalid } == "mode=block") {
          true
        } else {
          throw Invalid
        }
      } else {
        false
      }))
    }
  }
}
