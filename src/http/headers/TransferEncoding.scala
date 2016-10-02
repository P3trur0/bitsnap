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

final class TransferEncoding(val encoding: Encoding) extends Header {

  override val name = TransferEncoding.name

  override lazy val value = encoding.toString

  override def equals(that: Any) = that match {
    case that: TransferEncoding => that.encoding == this.encoding
    case _                      => false
  }

  override def hashCode = headers.hashCodePrime + encoding.hashCode
}

object TransferEncoding {

  private[http] final val name = "transfer-encoding"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String) = {
    if (string.isEmpty) { throw Invalid }

    new TransferEncoding(Encoding(string))
  }
}
