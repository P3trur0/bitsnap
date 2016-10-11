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

import scala.util.{Failure, Success, Try}

private[http] sealed class Encoding(val name: String) {
  override def toString = name

  override def equals(that: Any) = that match {
    case that: Encoding => this.name == that.name
    case _              => false
  }

  override def hashCode = http.hashCodePrime + name.hashCode
}

object Encoding {
  case object Any      extends Encoding("any")
  case object Compress extends Encoding("compress")
  case object Deflate  extends Encoding("deflate")
  case object Exi      extends Encoding("exi")
  case object Gzip     extends Encoding("gzip")
  case object Identity extends Encoding("identity")
  case object Pack200  extends Encoding("pack200-gzip")
  case object Br       extends Encoding("br")
  case object Bzip2    extends Encoding("bzip2")
  case object Lzma     extends Encoding("lzma")
  case object PeerDist extends Encoding("peerdist")
  case object Sdch     extends Encoding("sdch")
  case object Xpress   extends Encoding("xpress")
  case object Xz       extends Encoding("xz")
  case object Trailers extends Encoding("trailers")

  private[http] val known = List(
    Any,
    Compress,
    Deflate,
    Exi,
    Gzip,
    Identity,
    Pack200,
    Br,
    Bzip2,
    Lzma,
    PeerDist,
    Sdch,
    Xpress,
    Xz,
    Trailers
  )

  object Unknown extends Header.Invalid

  def apply(name: String): Try[Encoding] =
    known.find { _.name == name }.map { Success(_) }.getOrElse { Failure(Unknown) }

  def unapplySeq(string: String): Option[Seq[Encoding]] =
    Some(
      string
        .split(",")
        .map { e =>
          apply(e.trim)
        }
        .filter { _.isSuccess }
        .map { _.get })
}
