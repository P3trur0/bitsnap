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

import java.util.Locale

import io.bitsnap.http.Header.Implicit.{Date => HeaderDate, _}
import io.bitsnap.http.{Range => HttpRange}

class Accept(val types: QualityParameters[MimeType]) extends Header {

  if (types.isEmpty) {
    throw Accept.Invalid
  }

  override lazy val value = types.toString

  override val name = Accept.name

  def this(`type`: MimeType) {
    this(Seq(`type` -> 10))
  }

  override def equals(that: Any) = that match {
    case that: Accept => this.types == that.types
    case _            => false
  }

  override def hashCode = (0 /: types.map { _.hashCode }) { headers.hashCodePrime + _ + _ }
}

object Accept {

  private[http] final val name = "accept"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String) = {
    if (string.isEmpty) { throw Invalid }

    val Parameters(parameters @ _ *) = string

    val qualityParameters = parameters.map {
      _.toQualityParameter(MimeType(_), Invalid)
    }

    if (qualityParameters.isEmpty) {
      throw Invalid
    } else {
      new Accept(qualityParameters)
    }
  }
}

final class AcceptCharset(val charsets: QualityParameters[Charset]) extends Header {

  if (charsets.isEmpty) {
    throw AcceptCharset.Invalid
  }

  override val name       = AcceptCharset.name
  override lazy val value = charsets.toString

  def this(charset: Charset) {
    this(Seq(charset -> 10))
  }

  override def equals(that: Any) = that match {
    case that: AcceptCharset => this.charsets == that.charsets
    case _                   => false
  }

  override def hashCode = (0 /: charsets.map { _.hashCode }) { headers.hashCodePrime + _ + _ }
}

object AcceptCharset {

  private[http] final val name = "accept-charset"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String) = {
    if (string.isEmpty) { throw Invalid }

    val Parameters(parameters @ _ *) = string

    val qualityParameters = parameters.map {
      _.toQualityParameter(Charset(_), Invalid)
    }

    if (qualityParameters.isEmpty) {
      throw Invalid
    } else {
      new AcceptCharset(qualityParameters)
    }
  }
}

final class AcceptDatetime(override val date: HeaderDate) extends DateHeader(date) {
  override val name = AcceptDatetime.name

  override def equals(that: Any) = that match {
    case that: AcceptDatetime => super.equals(that)
    case _                    => false
  }

  override def hashCode = headers.hashCodePrime + super.hashCode
}

object AcceptDatetime {

  private[http] final val name = "accept-datetime"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String) = {
    if (string.isEmpty) { throw Invalid }

    val HeaderDate(date) = string
    new AcceptDatetime(date)
  }
}

final class AcceptEncoding(val encodings: QualityParameters[Encoding]) extends Header {

  // Trailers restricted only for use in TE header
  if (encodings.isEmpty || encodings.contains(Encoding.Trailers)) {
    throw AcceptEncoding.Invalid
  }

  override val name       = AcceptEncoding.name
  override lazy val value = encodings.toString

  def this(encoding: Encoding) {
    this(Seq(encoding -> 10))
  }

  override def equals(that: Any) = that match {
    case that: AcceptEncoding => this.encodings == that.encodings
    case _                    => false
  }

  override def hashCode = (0 /: encodings.map { _.hashCode }) { headers.hashCodePrime + _ + _ }
}

object AcceptEncoding {

  private[http] final val name = "accept-encoding"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String) = {
    if (string.isEmpty) { throw Invalid }

    val Parameters(parameters @ _ *) = string

    val qualityParameters = parameters.map {
      _.toQualityParameter(Encoding(_), Invalid)
    }

    if (qualityParameters.isEmpty) {
      throw Invalid
    } else {
      new AcceptEncoding(qualityParameters)
    }
  }
}

final class AcceptLanguage(val languages: QualityParameters[Locale]) extends Header {

  if (languages.isEmpty) {
    throw AcceptLanguage.Invalid
  }

  override val name       = AcceptLanguage.name
  override lazy val value = languages.toString

  def this(language: Locale) {
    this(Seq(language -> 10))
  }

  override def equals(that: Any) = that match {
    case that: AcceptLanguage => this.languages == that.languages
    case _                    => false
  }

  override def hashCode =
    (0 /: languages.map { _.hashCode }) { headers.hashCodePrime + _ + _ }
}

object AcceptLanguage {

  private[http] final val name = "accept-language"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String) = {
    if (string.isEmpty) { throw Invalid }

    val Parameters(parameters @ _ *) = string

    val qualityParameters = parameters.map { p =>
      p.toQualityParameter({ e =>
        val Locales(locale) = e; locale
      }, Invalid)
    }

    if (qualityParameters.isEmpty) {
      throw Invalid
    } else {
      new AcceptLanguage(qualityParameters)
    }
  }
}

final class AcceptPatch(types: QualityParameters[MimeType]) extends Accept(types) {

  if (types.isEmpty) {
    throw AcceptPatch.Invalid
  }

  override val name = AcceptPatch.name

  def this(`type`: MimeType) {
    this(Seq(`type` -> 10))
  }

  override def equals(that: Any) = that match {
    case that: AcceptPatch => super.equals(that)
    case _                 => false
  }

  override def hashCode = headers.hashCodePrime + super.hashCode
}

object AcceptPatch {

  private[http] final val name = "accept-patch"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String) = {
    if (string.isEmpty) { throw Invalid }

    val Parameters(parameters @ _ *) = string

    val qualityParameters = parameters.map {
      _.toQualityParameter(MimeType(_), Invalid)
    }

    if (qualityParameters.isEmpty) {
      throw Invalid
    } else {
      new AcceptPatch(qualityParameters)
    }
  }
}

class AcceptRanges(val unit: HttpRange.Unit) extends Header {

  override lazy val value = unit.toString
  override val name       = AcceptRanges.name

  override def equals(that: Any) = that match {
    case that: AcceptRanges => this.unit == that.unit
    case _                  => false
  }

  override def hashCode = headers.hashCodePrime + unit.hashCode
}

object AcceptRanges {

  private[http] final val name = "accept-ranges"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String) = {
    if (string.isEmpty) { throw Invalid }

    if (string.stripMargin(' ').isEmpty) {
      throw Invalid
    }

    new AcceptRanges(HttpRange.Unit(string))
  }
}

class TransferEncodings(encodings: Seq[Encoding]) extends Header {
  override val name: String = TransferEncodings.name

  override lazy val value: String = encodings.mkString(", ")
}

object TransferEncodings {

  private[http] final val name = "te"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String) = {
    if (string.isEmpty) { throw Invalid }

    val Encoding(encodings @ _ *) = string
    if (encodings.isEmpty) {
      throw Invalid
    }

    new TransferEncodings(encodings)
  }
}
