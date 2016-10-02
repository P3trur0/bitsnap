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

final class DoNotTrack(val isEnabled: Boolean) extends Header {

  override val name: String = DoNotTrack.name

  override val value: String = isEnabled.toString

  override def equals(that: Any) = that match {
    case that: DoNotTrack => this.isEnabled == that.isEnabled
    case _                => false
  }

  override def hashCode = headers.hashCodePrime + isEnabled.hashCode
}

object DoNotTrack {

  private[http] final val name = "dnt"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String) = string match {
    case "true"  => new DoNotTrack(true)
    case "false" => new DoNotTrack(false)
    case _       => throw Invalid
  }
}
