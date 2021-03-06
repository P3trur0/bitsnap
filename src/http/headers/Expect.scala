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

final class Expect(val status: Status) extends Header {

  override val name: String = Expect.name

  override lazy val value: String = s"${status.value}-${status.name}"

  override def equals(that: Any) = that match {
    case that: Expect => this.status == that.status
    case _            => false
  }

  override def hashCode = headers.hashCodePrime + status.hashCode
}

object Expect {

  private[http] final val name = "expect"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String): Try[Expect] = {
    if (string.isEmpty) {
      Failure(Invalid)
    } else {
      val chunks = string.split("-")
      if (chunks.size != 2) {
        Failure(Invalid)
      } else {
        Status(chunks.head.toInt).map { new Expect(_) } match {
          case Failure(e) if e.isInstanceOf[NumberFormatException] => Failure(Invalid)
          case Success(e)                                          => Success(e)
        }
      }
    }
  }
}
