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

final class MaxForwards(val numberOfForwards: Int) extends Header {
  if (numberOfForwards <= 0) {
    throw MaxForwards.Invalid
  }

  override val name: String = MaxForwards.name

  override val value: String = numberOfForwards.toString

  override def equals(that: Any) = that match {
    case that: MaxForwards => this.numberOfForwards == that.numberOfForwards
    case _                 => false
  }

  override def hashCode = headers.hashCodePrime + numberOfForwards.hashCode
}

object MaxForwards {

  private[http] final val name = "max-forwards"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String): Try[MaxForwards] = {
    Try(new MaxForwards(string.toInt)) match {
      case Failure(e) if e.isInstanceOf[NumberFormatException] => Failure(Invalid)
      case Success(e)                                          => Success(e)
    }
  }
}
