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

import scala.util.{Failure, Success, Try}
import scala.{None => ScalaNone, Range => ScalaRange}

private[http] abstract sealed class Range(val unit: Range.Unit, val size: Option[Int]) {

  def rangeToString: String

  override def toString: String =
    if (unit == Range.Unit.None) {
      s"$unit"
    } else {
      s"$unit $rangeToString/${size.getOrElse("*")}"
    }

  override def equals(obj: Any) = obj match {
    case obj: Range =>
      this.size == obj.size
    case _ => false
  }

  override def hashCode = (0 /: Seq(unit, size).map { _.hashCode }) {
    http.hashCodePrime * _ + _
  }
}

object Range {

  object None extends Range(Unit.None, ScalaNone) {
    override def rangeToString = s""
  }

  case class Bounded(range: ScalaRange, override val unit: Range.Unit, override val size: Option[Int])
      extends Range(unit, size) {
    def this(range: ScalaRange, unit: Range.Unit, size: Int) {
      this(range, unit, Some(size))
    }

    def this(range: ScalaRange, unit: Range.Unit) {
      this(range, unit, ScalaNone)
    }

    override def rangeToString = s"${range.start}-${range.end}"

    override def equals(obj: Any) = obj match {
      case obj: Range.Bounded =>
        this.range == obj.range && super.equals(obj)
      case _ => false
    }

    override def hashCode = range.hashCode + super.hashCode
  }

  case class Head(prefixEnd: Int, override val unit: Range.Unit, override val size: Option[Int])
      extends Range(unit, size) {

    def this(prefixEnd: Int, unit: Range.Unit, size: Int) {
      this(prefixEnd, unit, Some(size))
    }

    def this(prefixEnd: Int, unit: Range.Unit) {
      this(prefixEnd, unit, ScalaNone)
    }

    override def rangeToString = s"-$prefixEnd"

    override def equals(obj: Any) = obj match {
      case obj: Range.Head =>
        this.prefixEnd == obj.prefixEnd && super.equals(obj)
      case _ => false
    }

    override def hashCode = prefixEnd.hashCode + super.hashCode
  }

  case class Tail(suffixStart: Int, override val unit: Range.Unit, override val size: Option[Int])
      extends Range(unit, size) {

    def this(suffixStart: Int, unit: Range.Unit, size: Int) {
      this(suffixStart, unit, Some(size))
    }

    def this(suffixStart: Int, unit: Range.Unit) {
      this(suffixStart, unit, ScalaNone)
    }

    override def rangeToString = s"$suffixStart-"

    override def equals(obj: Any) = obj match {
      case obj: Range.Tail =>
        this.suffixStart == obj.suffixStart && super.equals(obj)
      case _ => false
    }

    override def hashCode = suffixStart.hashCode + super.hashCode
  }

  private[http] sealed class Unit(val name: String) {
    override def toString = name

    override def equals(obj: Any) = obj match {
      case obj: Unit => this.name == obj.name
      case _         => false
    }

    override def hashCode = 31 * name.hashCode
  }

  object Invalid extends Header.Invalid

  private[http] def apply(unit: Unit, rangeSizeString: String): Try[Range] =
    try {
      if (!rangeSizeString.contains('/')) {
        throw Invalid
      }

      val rangeSizeChunks = rangeSizeString.split('/')
      val rangeString = rangeSizeChunks.headOption.getOrElse {
        throw Invalid
      }

      val sizeString = rangeSizeChunks.lastOption.getOrElse {
        throw Invalid
      }

      val size = if (sizeString == "*") {
        ScalaNone
      } else {
        Some(sizeString.toInt)
      }

      rangeString match {
        case _ if rangeString.startsWith("-") =>
          val suffix = rangeString.stripMargin('-').toInt
          if (suffix <= 0) {
            throw Invalid
          }

          Success(new Tail(suffix, unit, size))
        case _ if rangeString.endsWith("-") =>
          val prefix = rangeString.stripMargin('-').toInt
          if (prefix <= 0) {
            throw Invalid
          }

          Success(new Head(prefix, unit, size))
        case _ =>
          val chunks = rangeString.split('-')
          val start  = chunks.headOption.getOrElse { throw Invalid }.toInt
          val end    = chunks.lastOption.getOrElse { throw Invalid }.toInt

          Success(new Bounded(start to end, unit, size))
      }
    } catch {
      case e: NumberFormatException => throw Invalid
      case Invalid                  => Failure(Invalid)
    }

  def apply(string: String): Try[Range] = {
    val chunks = string.split(" ")

    if (chunks.length != 2) {
      Failure(Invalid)
    } else {
      Unit(chunks(0)) match {
        case Success(Unit.None) => Success(Range.None)
        case Success(u)         => Range.apply(u, chunks(1))
        case Failure(_)         => Failure(Invalid)
      }
    }
  }

  object Unit {
    object Invalid extends Header.Invalid

    object Bytes                       extends Range.Unit("bytes")
    object None                        extends Range.Unit("none")
    final class Specific(name: String) extends Range.Unit(name)

    def apply(name: String): Try[Unit] = name match {
      case "bytes"           => Success(Unit.Bytes)
      case "none"            => Success(Unit.None)
      case _ if name.isEmpty => Failure(Invalid)
      case _                 => Success(new Range.Unit.Specific(name))
    }
  }
}
