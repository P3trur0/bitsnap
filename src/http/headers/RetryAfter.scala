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

trait RetryAfter extends Header {

  def isSeconds = isInstanceOf[RetryAfterSeconds]

  def asSeconds =
    if (isSeconds) {
      Some(asInstanceOf[RetryAfterSeconds])
    } else {
      None
    }

  def isDate = isInstanceOf[RetryAfterDate]

  def asDate =
    if (isDate) {
      Some(asInstanceOf[RetryAfterDate])
    } else {
      None
    }
}

final class RetryAfterSeconds(val seconds: Int) extends Header with RetryAfter {

  if (seconds <= 0) {
    throw RetryAfter.Invalid
  }

  override val name = RetryAfter.name

  override val value = seconds.toString

  override def equals(that: Any) = that match {
    case that: RetryAfterSeconds => this.seconds == that.seconds
    case _                       => false
  }

  override def hashCode = headers.hashCodePrime + seconds.hashCode
}

final class RetryAfterDate(val date: HeaderDate) extends Header with RetryAfter {
  override val name = RetryAfter.name

  override val value = date.toString

  override def equals(that: Any) = that match {
    case that: RetryAfterDate => this.date == that.date
    case _                    => false
  }

  override def hashCode = headers.hashCodePrime + date.hashCode
}

object RetryAfter {
  private[http] final val name = "retry-after"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String) = {
    if (string.isEmpty) { throw Invalid }

    try {
      val HeaderDate(date) = string
      new RetryAfterDate(date)
    } catch {
      case HeaderDate.Invalid =>
        try {
          new RetryAfterSeconds(string.toInt)
        } catch {
          case _: NumberFormatException => throw Invalid
        }
    }
  }
}
