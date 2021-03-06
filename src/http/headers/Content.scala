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

import io.bitsnap.http.Header._
import io.bitsnap.http.{Range => HttpRange}

import scala.util.{Failure, Success, Try}

final class ContentDisposition(val dispositionType: Parameter) extends Header with ContentDisposition.Checks {
  override val name = ContentDisposition.name

  def filename = dispositionType.attributes get "filename"

  override lazy val value = dispositionType.toString

  override def equals(obj: Any) = obj match {
    case obj: ContentDisposition => obj.dispositionType == this.dispositionType
    case _                       => false
  }

  override def hashCode = headers.hashCodePrime + dispositionType.hashCode
}

object ContentDisposition {

  trait Checks { this: ContentDisposition =>

    final def isAttachment = dispositionType.name == "attachment"
  }

  private[http] final val name = "content-disposition"

  object Invalid extends Header.Invalid

  def attachment(attributes: Map[String, String]): ContentDisposition =
    new ContentDisposition(new Header.Parameter("attachment", attributes))

  def attachment(filename: String): ContentDisposition = attachment(Map("filename" -> filename))

  def apply(string: String): Try[ContentDisposition] = {
    if (string.trim.isEmpty) {
      Failure(Invalid)
    } else {
      val Parameters(parameters @ _ *) = string

      parameters.isEmpty match {
        case true => Failure(Invalid)
        case _    => Success(new ContentDisposition(parameters.head))
      }
    }
  }
}

final class ContentEncoding(val encoding: Encoding) extends Header {

  override val name = ContentEncoding.name

  override lazy val value = encoding.toString

  override def equals(that: Any) = that match {
    case that: ContentEncoding => this.encoding == that.encoding
    case _                     => false
  }

  override def hashCode = headers.hashCodePrime + encoding.hashCode
}

object ContentEncoding {
  private[http] final val name = "content-encoding"

  object Invalid extends Header.Invalid

  def apply(string: String): Try[ContentEncoding] = {
    if (string.trim.isEmpty) {
      Failure(Invalid)
    } else {
      Encoding(string).map(new ContentEncoding(_))
    }
  }
}

final class ContentLanguage(val locales: Locales) extends Header {
  override val name = ContentLanguage.name

  override lazy val value = locales.toString

  override def equals(that: Any) = that match {
    case that: ContentLanguage => this.locales == that.locales
    case _                     => false
  }

  override def hashCode = headers.hashCodePrime + locales.hashCode
}

object ContentLanguage {

  private[http] final val name = "content-language"

  object Invalid extends Header.Invalid

  def apply(string: String): Try[ContentLanguage] = {
    if (string.trim.isEmpty) {
      Failure(Invalid)
    } else {
      val Locales(locales @ _ *) = string

      locales.isEmpty match {
        case true => Failure(Invalid)
        case _    => Success(new ContentLanguage(new Locales(locales)))
      }
    }
  }
}

final class ContentLength(val length: Int) extends Header {

  if (length <= 0) {
    throw ContentLength.Invalid
  }

  override val name: String = ContentLength.name

  override lazy val value: String = length.toString

  override def equals(that: Any) = that match {
    case that: ContentLength => this.length == that.length
    case _                   => false
  }

  override def hashCode = headers.hashCodePrime + length.hashCode
}

object ContentLength {

  private[http] final val name = "content-length"

  object Invalid extends Header.Invalid

  def apply(string: String): Try[ContentLength] = {
    if (string.trim.isEmpty) {
      Failure(Invalid)
    } else {
      Try(new ContentLength(string.toInt)) match {
        case Failure(e) if e.isInstanceOf[NumberFormatException] => Failure(Invalid)
        case Success(e)                                          => Success(e)
      }
    }
  }
}

// TODO: URI
final class ContentLocation(val url: String) extends Header {
  override val name: String = ContentLocation.name

  override val value: String = url

  override def equals(that: Any) = that match {
    case that: ContentLocation => this.url == that.url
    case _                     => false
  }

  override def hashCode = headers.hashCodePrime + url.hashCode
}

object ContentLocation {
  private[http] final val name = "content-location"

  object Invalid extends Header.Invalid

  def apply(string: String): Try[ContentLocation] = {
    val trimmed = string.trim
    if (trimmed.isEmpty) {
      Failure(Invalid)
    } else {
      Success(new ContentLocation(trimmed))
    }
  }
}

final class ContentMD5(val md5: String) extends Header {

  if (!ContentMD5.md5Pattern.pattern.matcher(md5).matches) {
    throw ContentMD5.Invalid
  }

  override val name: String = ContentMD5.name

  override val value: String = md5

  override def equals(that: Any) = that match {
    case that: ContentMD5 => this.md5 == that.md5
    case _                => false
  }

  override def hashCode = headers.hashCodePrime + md5.hashCode
}

object ContentMD5 {

  private[http] final val name = "content-md5"

  object Invalid extends Header.Invalid

  private[headers] val md5Pattern = """([a-fA-F0-9]{32})""".r

  def apply(string: String): Try[ContentMD5] = {
    val trimmed = string.trim
    if (trimmed.isEmpty) {
      Failure(Invalid)
    } else {
      Success(new ContentMD5(string))
    }
  }
}

final class ContentRange(val range: HttpRange) extends Header {
  override val name: String = ContentRange.name

  override lazy val value: String = range.toString

  override def equals(that: Any) = that match {
    case that: ContentRange => this.range == that.range
    case _                  => false
  }

  override def hashCode = headers.hashCodePrime + range.hashCode
}

object ContentRange {

  private[http] final val name = "content-range"

  object Invalid extends Header.Invalid

  def apply(string: String): Try[ContentRange] = {
    val trimmed = string.trim
    if (trimmed.isEmpty) {
      Failure(Invalid)
    } else {
      HttpRange(trimmed).map { new ContentRange(_) }
    }
  }
}

final class ContentType(val `type`: MimeType) extends Header {
  override val name: String = ContentType.name

  override lazy val value: String = `type`.toString

  override def equals(that: Any) = that match {
    case that: ContentType => this.`type` == that.`type`
    case _                 => false
  }

  override def hashCode = headers.hashCodePrime + `type`.hashCode
}

object ContentType {

  private[http] final val name = "content-type"

  object Invalid extends Header.Invalid

  def apply(string: String): Try[ContentType] = {
    val trimmed = string.trim
    if (trimmed.isEmpty) {
      Failure(Invalid)
    } else {
      MimeType(trimmed).map { new ContentType(_) }
    }
  }
}
