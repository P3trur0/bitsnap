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

import io.bitsnap.http.{Range => HttpRange}

import scala.util.{Failure, Try}

final class Range(val range: HttpRange) extends Header {

  override val name = Range.name

  override lazy val value = range.toString

  override def equals(that: Any) = that match {
    case that: Range => this.range == that.range
    case _           => false
  }

  override def hashCode = headers.hashCodePrime + range.hashCode
}

object Range {
  private[http] final val name = "range"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String): Try[Range] = {
    if (string.trim.isEmpty) {
      Failure(Invalid)
    } else {
      HttpRange(string).map { new Range(_) }
    }
  }
}
