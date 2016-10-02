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

final class Etag(override val isWeak: Boolean, override val tag: String) extends WeakHeader(isWeak, tag) {
  override val name: String = IfMatch.name

  override def equals(that: Any) = that match {
    case that: Etag => super.equals(that)
    case _          => false
  }

  override def hashCode = headers.hashCodePrime + super.hashCode
}

object Etag {

  private[http] final val name = "etag"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String) = {
    if (string.isEmpty) { throw Invalid }

    val (isWeak, tag) = WeakHeader(string)
    new IfMatch(isWeak, tag)
  }
}
