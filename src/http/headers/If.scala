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

import io.bitsnap.http.Header.Implicit.{Date => HeaderDate}

final class IfMatch(override val isWeak: Boolean, override val tag: String) extends WeakHeader(isWeak, tag) {

  if (tag.isEmpty) {
    throw IfMatch.Invalid
  }

  override val name: String = IfMatch.name

  override def equals(that: Any) = that match {
    case that: IfMatch => super.equals(that)
    case _             => false
  }

  override def hashCode = headers.hashCodePrime + super.hashCode
}

object IfMatch {

  private[http] final val name = "if-match"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String) = {
    if (string.isEmpty) { throw Invalid }

    val (isWeak, tag) = WeakHeader(string)
    new IfMatch(isWeak, tag)
  }
}

final class IfModifiedSince(override val date: HeaderDate) extends DateHeader(date) {

  override val name = IfModifiedSince.name

  override def equals(that: Any) = that match {
    case that: IfModifiedSince => super.equals(that)
    case _                     => false
  }

  override def hashCode = headers.hashCodePrime + super.hashCode
}

object IfModifiedSince {

  private[http] final val name = "if-modified-since"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String) = {
    if (string.isEmpty) { throw Invalid }

    val HeaderDate(date) = string
    new IfModifiedSince(date)
  }
}

final class IfNoneMatch(override val isWeak: Boolean, override val tag: String) extends WeakHeader(isWeak, tag) {

  if (tag.isEmpty) {
    throw IfNoneMatch.Invalid
  }

  override val name = IfNoneMatch.name

  override def equals(that: Any) = that match {
    case that: IfNoneMatch => super.equals(that)
    case _                 => false
  }

  override def hashCode = headers.hashCodePrime + super.hashCode
}

object IfNoneMatch {

  private[http] final val name = "if-none-match"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String) = {
    if (string.isEmpty) { throw Invalid }

    val (isWeak, tag) = WeakHeader(string)
    new IfNoneMatch(isWeak, tag)
  }
}

trait IfRange extends Header {

  def isTag = isInstanceOf[IfRangeTag]

  def asTag =
    if (isTag) {
      Some(this.asInstanceOf[IfRangeTag])
    } else {
      None
    }

  def isDate = isInstanceOf[IfRangeDate]

  def asDate =
    if (isDate) {
      Some(this.asInstanceOf[IfRangeDate])
    } else {
      None
    }
}

object IfRange {
  private[http] final val name = "if-range"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String) = {
    if (string.isEmpty) { throw Invalid }

    try {
      val HeaderDate(date) = string
      new IfRangeDate(date)
    } catch {
      case HeaderDate.Invalid =>
        val (isWeak, tag) = WeakHeader(string)
        new IfRangeTag(isWeak, tag)
    }
  }
}

final class IfRangeTag(override val isWeak: Boolean, override val tag: String)
    extends WeakHeader(isWeak, tag)
    with IfRange {

  if (tag.isEmpty) {
    throw IfNoneMatch.Invalid
  }

  override val name = IfRange.name

  override def equals(that: Any) = that match {
    case that: IfRangeTag => super.equals(that)
    case _                => false
  }

  override def hashCode = headers.hashCodePrime + super.hashCode
}

final class IfRangeDate(override val date: HeaderDate) extends DateHeader(date) with IfRange {

  override val name: String = IfRange.name

  override def equals(that: Any) = that match {
    case that: IfRangeDate => super.equals(that)
    case _                 => false
  }

  override def hashCode = headers.hashCodePrime + super.hashCode
}

final class IfUnmodifiedSince(override val date: HeaderDate) extends DateHeader(date) {

  override val name = IfUnmodifiedSince.name

  override def equals(that: Any) = that match {
    case that: IfUnmodifiedSince => super.equals(that)
    case _                       => false
  }

  override def hashCode = headers.hashCodePrime + super.hashCode
}

object IfUnmodifiedSince {

  private[http] final val name = "if-unmodified-since"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String) = {
    if (string.isEmpty) { throw Invalid }

    val HeaderDate(date) = string
    new IfUnmodifiedSince(date)
  }
}
