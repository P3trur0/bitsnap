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

import scala.util.{Failure, Success, Try}

sealed class Vary(final val headerName: String) extends Header {
  override val name: String = Vary.name
  override lazy val value   = headerName

  override def equals(that: Any) = that match {
    case that: Vary => this.headerName == that.headerName
    case _          => false
  }

  override def hashCode = headers.hashCodePrime + headerName.hashCode
}

object VaryAny extends Vary("*") {

  override def equals(that: Any) = that match {
    case VaryAny => true
    case Vary    => super.equals(that)
    case _       => false
  }

  override def hashCode = headers.hashCodePrime + name.hashCode + value.hashCode
}

object Vary {
  private[http] final val name = "vary"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String): Try[Vary] = string match {
    case "*"                      => Success(VaryAny)
    case _ if string.trim.isEmpty => Failure(Invalid)
    case _                        => Success(new Vary(string))
  }
}
