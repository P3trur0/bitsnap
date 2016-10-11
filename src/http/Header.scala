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

import java.time.format.{DateTimeFormatter, DateTimeParseException}
import java.time.{ZoneId, ZonedDateTime}
import java.util.{Locale, Date => JavaDate}

import io.bitsnap.util._

import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

trait Header extends Ordered[String] {
  val name: String
  val value: String

  override def compare(that: String) = name compare that

  override def toString = s"$name: $value"
}

abstract class WeakHeader(val isWeak: Boolean, val tag: String) extends Header {
  override lazy val value = if (isWeak) {
    s"\\W${tag.quoted}"
  } else {
    tag
  }

  override def equals(that: Any) = that match {
    case that: WeakHeader => this.isWeak == that.isWeak && this.tag == that.tag
    case _                => false
  }

  override def hashCode = http.hashCodePrime + isWeak.hashCode + tag.hashCode
}

object WeakHeader {
  def apply(string: String) = {
    (string.startsWith("\\W") || string.startsWith("\\w"), string.stripPrefixIgnoreCase("\\W").stripQuotes)
  }
}

abstract class DateHeader(val date: Header.Date) extends Header {
  override lazy val value: String = date.toString

  override def equals(that: Any) = that match {
    case that: DateHeader => this.date == that.date
    case _                => false
  }

  override def hashCode = http.hashCodePrime + date.hashCode
}

object Header {

  class Parameter(val name: String, val attributes: Map[String, String]) extends Parameter.Transformations {
    override def toString =
      s"$name${("" /: attributes) { (sum, e) =>
        s"$sum;${e._1}=${e._2}"
      }}"

    override def equals(other: Any) = other match {
      case that: Parameter => name == that.name && attributes == that.attributes
      case _               => false
    }

    override def hashCode =
      (0 /: attributes.map { _.hashCode }) { http.hashCodePrime * _ + _ }
  }

  private[http] object Parameter {

    trait Transformations { this: Parameter =>

      private[http] final def toQualityParameter[T](supplementary: (String) => T,
                                                    invalid: Header.Invalid): Try[(T, Int)] = {
        try {
          val q = attributes.getOrElse("q", "1.0").toFloat * 10
          Success(supplementary(name) -> q.toInt)
        } catch {
          case e: NumberFormatException => Failure(invalid)
        }
      }
    }
  }

  class QualityParameters[T](val from: Seq[(T, Int)]) extends AnyVal {

    override def toString =
      new Parameters(from.map { x =>
        new Parameter(x._1.toString, if (x._2 > 0 && x._2 < 10) {
          Map("q" -> s"0.${x._2}")
        } else { Map() })
      }).toString
  }

  class Locales(val from: Seq[Locale]) extends AnyVal {
    override def toString = from.mkString("", ", ", "")
  }

  object Locales {
    def unapplySeq(string: String): Option[Seq[Locale]] = {
      val locales = string.split(",") map { s =>
        if (s contains '_') {
          val e                   = s split "_"
          val (country, language) = (e(0).trim, e(1).trim)
          if (country.isEmpty || language.isEmpty) {
            None
          } else { Some(new Locale(country, language)) }
        } else {
          val language = string stripMargin ' '
          if (language.isEmpty) {
            None
          } else { Some(new Locale(language)) }
        }
      } filter { _.isDefined } map { _.get }

      if (locales.isEmpty) {
        None
      } else { Some(locales) }
    }
  }

  class Parameters(val from: Seq[Parameter]) extends AnyVal {
    override def toString = from.mkString("", ", ", "")
  }

  object Parameters {

    def unapplySeq(string: String): Option[Seq[Parameter]] = {
      val parameters = string
        .split(',')
        .map { e =>
          val attrChunks = e.split(';')
          val name       = attrChunks.head.trim
          if (name.isEmpty) {
            None
          } else {
            Some(new Parameter(name, attrChunks.tail.map {
              _.splitNameValue
            }.filter { _.isDefined }.map { _.get }.toMap))
          }
        }
        .filter { _.isDefined }
        .map { _.get }

      if (parameters.isEmpty) {
        None
      } else { Some(parameters) }
    }
  }

  class Date(val from: JavaDate) extends AnyVal {
    override def toString = Date.format(from)
  }

  object Date {

    object Invalid extends Header.Invalid

    private[http] def dateFormatter = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneId of "GMT")

    private[http] def format(from: JavaDate) = Date.dateFormatter.format(from.toInstant.atZone(ZoneId of "GMT"))

    def unapply(string: String) =
      try {
        Some(JavaDate.from(ZonedDateTime.parse(string, dateFormatter).toInstant))
      } catch {
        case e: IllegalArgumentException => None
        case e: DateTimeParseException   => None
      }

    def apply(string: String): Try[Date] = unapply(string) match {
      case Some(date) => Success(new Date(date))
      case None       => Failure(Invalid)
    }
  }

  private[http] object Implicit {
    implicit def toQualityParameters[T](from: Seq[(T, Int)]): QualityParameters[T] =
      new QualityParameters[T](from)

    implicit def fromQualityParameters[T](parameters: QualityParameters[T]): Seq[(T, Int)] =
      parameters.from

    implicit def toLocales(from: Seq[Locale]): Locales = new Locales(from)

    implicit def fromLocales(locales: Locales): Seq[Locale] = locales.from

    implicit def toParameters(from: Seq[Parameter]): Parameters = new Parameters(from)

    implicit def fromParameters(parameters: Parameters): Seq[Parameter] = parameters.from

    implicit def toDate(from: JavaDate): Date = new Date(from)

    implicit def fromDate(date: Date): JavaDate = date.from
  }

  private[http] class Invalid extends Http.Invalid
}
