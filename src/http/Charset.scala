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

import java.nio.charset.{StandardCharsets, Charset => NioCharset}

import scala.util.{Failure, Success, Try}

private[http] sealed class Charset(private[http] val charset: NioCharset, private[http] val aliases: List[String]) {

  override def toString = aliases.head

  def apply = charset

  def decoder = charset.newDecoder

  def encoder = charset.newEncoder

  override def equals(that: Any) = that match {
    case that: Charset =>
      this.charset == that.charset && this.hashCode == that.hashCode
    case _ => false
  }

  override def hashCode = (0 /: Seq(charset, aliases).map { _.hashCode }) { http.hashCodePrime + _ + _ }
}

object Charset {

  case object ASCII extends Charset(StandardCharsets.US_ASCII, List("ASCII"))

  case object ANSI extends Charset(NioCharset forName "CP1252", List("windows-1252", "CP1252"))

  case object ISO8859 extends Charset(StandardCharsets.ISO_8859_1, List("ISO-8859-1", "ISO8859"))

  case object UTF8 extends Charset(StandardCharsets.UTF_8, List("UTF-8", "UTF8"))

  case object UTF16 extends Charset(StandardCharsets.UTF_16, List("UTF16", "UTF16"))

  case object UTF32 extends Charset(NioCharset forName "UTF-32", List("UTF-32", "UTF32"))

  private[http] val known = List(
    ASCII,
    ANSI,
    ISO8859,
    UTF8,
    UTF16,
    UTF32
  )

  object Unknown extends Header.Invalid

  def apply(name: String): Try[Charset] =
    known find { charset =>
      charset.aliases.contains(name)
    } match {
      case Some(charset) => Success(charset)
      case None          => Failure(Unknown)
    }
}
