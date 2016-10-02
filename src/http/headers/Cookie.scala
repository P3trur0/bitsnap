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

final class Cookie(val values: Map[String, String]) extends Header {
  override val name = Cookie.name

  override lazy val value = values.map { e =>
    s"${e._1}=${e._2}"
  }.mkString("; ")

  def get(key: String) = values.get(key)

  def getOrElse(key: String, e: => String) = values.getOrElse(key, e)

  override def equals(that: Any) = that match {
    case that: Cookie => this.values == that.values
    case _            => false
  }

  override def hashCode() =
    (0 /: values.map { e =>
      e._1.hashCode + e._2.hashCode
    }) { headers.hashCodePrime + _ + _ }
}

object Cookie {
  private[http] final val name = "cookie"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String) = {
    if (string.isEmpty) { throw Invalid }

    new Cookie(
      string
        .split(";")
        .map {
          _.trim
        }
        .map { e =>
          e.splitNameValue.getOrElse { throw Invalid }
        }
        .toMap)
  }
}
