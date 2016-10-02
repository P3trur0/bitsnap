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
package headers

final class AccessControlAllowCredentials(val areAllowed: Boolean) extends Header {
  override val name: String = AccessControlAllowCredentials.name

  override lazy val value: String = areAllowed.toString

  override def equals(that: Any) = that match {
    case that: AccessControlAllowCredentials => this.areAllowed == that.areAllowed
    case _                                   => false
  }

  override def hashCode = headers.hashCodePrime + areAllowed.hashCode
}

object AccessControlAllowCredentials {
  private[http] final val name = "access-control-allow-credentials"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String) = string.toLowerCase.trim match {
    case "true"  => new AccessControlAllowCredentials(true)
    case "false" => new AccessControlAllowCredentials(false)
    case _       => throw Invalid
  }
}

final class AccessControlAllowHeaders(val headerNames: Seq[String]) extends Header {

  if (headerNames.isEmpty) {
    throw AccessControlAllowHeaders.Invalid
  }

  override val name: String = AccessControlAllowHeaders.name

  override lazy val value: String = headerNames.mkString("", ", ", "")

  override def equals(that: Any) = that match {
    case that: AccessControlAllowHeaders => this.headerNames == that.headerNames
    case _                               => false
  }

  override def hashCode = (0 /: headerNames.map { _.hashCode }) { headers.hashCodePrime + _ + _ }
}

object AccessControlAllowHeaders {
  private[http] final val name = "access-control-allow-headers"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String) = {
    if (string.isEmpty) { throw Invalid }

    val headerNames = string split "," map { _.trim } filterNot { _.isEmpty }
    if (headerNames.isEmpty) {
      throw Invalid
    } else {
      new AccessControlAllowHeaders(headerNames)
    }
  }
}

final class AccessControlAllowMethods(val methods: Seq[Method]) extends Header {

  if (methods.isEmpty) {
    throw AccessControlAllowMethods.Invalid
  }

  override val name: String = AccessControlAllowMethods.name

  override lazy val value: String = methods.mkString("", ", ", "")

  override def equals(that: Any) = that match {
    case that: AccessControlAllowMethods => this.methods == that.methods
    case _                               => false
  }

  override def hashCode = (0 /: methods.map { _.hashCode }) { headers.hashCodePrime + _ + _ }
}

object AccessControlAllowMethods {
  private[http] final val name = "access-control-allow-methods"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String) = {
    if (string.isEmpty) { throw Invalid }

    try {
      new AccessControlAllowMethods(string split ',' map { i =>
        Method(i.trim)
      })
    } catch {
      case Method.Unknown => throw Invalid
    }
  }
}

final class AccessControlAllowOrigin(val origin: String) extends Header {

  if (origin.isEmpty || origin.contains(" ") || origin.contains("\t") || origin.contains("\n")) {
    throw AccessControlAllowOrigin.Invalid
  }

  override val name: String = AccessControlAllowOrigin.name

  override val value: String = origin

  def isAny = origin == "*"

  override def equals(that: Any) = that match {
    case that: AccessControlAllowOrigin => this.origin == that.origin
    case _                              => false
  }

  override def hashCode = headers.hashCodePrime + origin.hashCode
}

object AccessControlAllowOrigin {
  private[http] final val name = "access-control-allow-origin"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String) = {
    if (string.isEmpty) { throw Invalid }

    new AccessControlAllowOrigin(string)
  }
}

final class AccessControlExposeHeaders(val headerNames: Seq[String]) extends Header {

  if (headerNames.isEmpty) {
    throw AccessControlExposeHeaders.Invalid
  }

  override val name: String = AccessControlExposeHeaders.name

  override lazy val value: String = headerNames.mkString("", ", ", "")

  override def equals(that: Any) = that match {
    case that: AccessControlExposeHeaders => this.headerNames == that.headerNames
    case _                                => false
  }

  override def hashCode = (0 /: headerNames.map { _.hashCode }) { headers.hashCodePrime + _ + _ }
}

object AccessControlExposeHeaders {
  private[http] final val name = "access-control-expose-headers"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String) = {
    if (string.isEmpty) { throw Invalid }

    val headerNames = string split ',' map { _.trim } filterNot { _.isEmpty }
    if (headerNames.isEmpty) {
      throw Invalid
    } else {
      new AccessControlExposeHeaders(headerNames)
    }
  }
}

final class AccessControlMaxAge(val deltaSeconds: Int) extends Header {

  if (deltaSeconds <= 0) {
    throw AccessControlExposeHeaders.Invalid
  }

  override val name = AccessControlMaxAge.name

  override lazy val value = deltaSeconds.toString

  override def equals(that: Any) = that match {
    case that: AccessControlMaxAge => this.deltaSeconds == that.deltaSeconds
    case _                         => false
  }

  override def hashCode = headers.hashCodePrime + deltaSeconds.hashCode
}

object AccessControlMaxAge {
  private[http] final val name = "access-control-max-age"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String) = {
    if (string.isEmpty) { throw Invalid }

    val deltaSeconds = string.toInt
    if (deltaSeconds <= 0) {
      throw Invalid
    } else {
      new AccessControlMaxAge(deltaSeconds)
    }
  }
}

final class AccessControlRequestHeaders(val headerNames: Seq[String]) extends Header {

  if (headerNames.isEmpty) {
    throw AccessControlRequestHeaders.Invalid
  }

  override val name: String = AccessControlRequestHeaders.name

  override lazy val value: String = headerNames.mkString("", ", ", "")

  override def equals(that: Any) = that match {
    case that: AccessControlRequestHeaders => this.headerNames == that.headerNames
    case _                                 => false
  }

  override def hashCode = (0 /: headerNames.map { _.hashCode }) { headers.hashCodePrime + _ + _ }
}

object AccessControlRequestHeaders {
  private[http] final val name = "access-control-request-headers"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String) = {
    if (string.isEmpty) { throw Invalid }

    val headerNames = string split ',' map { _.trim } filterNot { _.isEmpty }
    if (headerNames.isEmpty) {
      throw Invalid
    } else {
      new AccessControlRequestHeaders(headerNames)
    }
  }
}

final class AccessControlRequestMethod(val method: Method) extends Header {
  override val name: String = AccessControlRequestMethod.name

  override lazy val value: String = method.toString

  override def equals(that: Any) = that match {
    case that: AccessControlRequestMethod => this.method == that.method
    case _                                => false
  }

  override def hashCode = headers.hashCodePrime + method.hashCode
}

object AccessControlRequestMethod {
  private[http] final val name = "access-control-request-method"

  override def toString = name

  def apply(string: String) = new AccessControlRequestMethod(Method(string))
}
