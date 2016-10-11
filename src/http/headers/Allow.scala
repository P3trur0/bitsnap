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

final class Allow(val methods: Seq[Method]) extends Header {

  override val name = Allow.name

  override lazy val value = methods.mkString("", ", ", "")

  override def equals(that: Any) = that match {
    case that: Allow => this.methods == that.methods
    case _           => false
  }

  override def hashCode = (0 /: methods.map { _.hashCode }) { headers.hashCodePrime + _ + _ }
}

object Allow {
  final val name = "allow"

  object Invalid extends Header.Invalid

  def apply(string: String): Try[Allow] = {
    if (string.trim.isEmpty) {
      Failure(Invalid)
    } else {
      val Method(methods @ _ *) = string

      methods.isEmpty match {
        case true => Failure(Invalid)
        case _    => Success(new Allow(methods))
      }
    }
  }
}
