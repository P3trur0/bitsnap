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

final class Forwarded(val directives: Seq[Forwarded.Directive]) extends Header {
  override val name: String = Forwarded.name

  override lazy val value: String = directives.mkString("; ")

  override def equals(that: Any) = that match {
    case that: Forwarded => that.directives == this.directives
    case _               => false
  }

  override def hashCode = headers.hashCodePrime + (0 /: directives.map { _.hashCode }) {
    headers.hashCodePrime + _ + _
  }
}

object Forwarded {
  private[http] final val name = "forwarded"

  override def toString = name

  object Invalid extends Header.Invalid

  sealed abstract class Directive(protected val name: String, val hasQuotes: Boolean) {
    val value: String

    override def toString =
      s"$name=${if (hasQuotes) {
        value.quoted
      } else { value }}"

    override def equals(that: Any) = that match {
      case that: Directive => this.name == that.name && this.value == that.value
      case _               => false
    }

    override def hashCode = headers.hashCodePrime + name.hashCode + value.hashCode
  }

  case class For(override final val value: String) extends Directive("for", true)

  case class Host(override final val value: String) extends Directive("host", true)

  case class By(override final val value: String) extends Directive("by", true)

  case class Protocol(override final val value: String) extends Directive("proto", false)

  object Directive {

    object Invalid extends Header.Invalid

    def apply(string: String): Try[Directive] = {
      val (name, value) = string.splitNameValue.getOrElse { throw Invalid }
      name.toLowerCase match {
        case "for"   => Success(For(value.stripQuotes))
        case "host"  => Success(Host(value.stripQuotes))
        case "by"    => Success(By(value.stripQuotes))
        case "proto" => Success(Protocol(value))
        case _       => Failure(Invalid)
      }
    }
  }

  def apply(string: String): Try[Forwarded] = {
    if (string.trim.isEmpty) {
      Failure(Invalid)
    } else {
      val directives = string.split(";").map { _.trim }.map { Directive(_) }.filter { _.isSuccess }.map { _.get }
      directives.isEmpty match {
        case true => Failure(Invalid)
        case _    => Success(new Forwarded(directives))
      }
    }
  }
}
