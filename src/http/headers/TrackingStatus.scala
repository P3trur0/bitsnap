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

final class TrackingStatus(val x: TrackingStatus.Value) extends Header {

  override val name = TrackingStatus.name

  override lazy val value = x.toString

  override def equals(that: Any) = that match {
    case that: TrackingStatus => that.x == this.x
    case _                    => false
  }

  override def hashCode = headers.hashCodePrime + x.hashCode

}

object TrackingStatus {
  private[http] final val name = "tsv"

  override def toString = name

  object Invalid extends Header.Invalid

  sealed abstract class Value(private[http] val flag: Char) {
    override def toString = flag.toString

    override def equals(that: Any) = that match {
      case that: Value => that.flag == this.flag
      case _           => false
    }

    override def hashCode = headers.hashCodePrime + flag.toInt
  }

  object UnderConstruction extends Value('!')

  object Dynamic extends Value('?')

  object Gateway extends Value('G')

  object NotTracking extends Value('N')

  object TrackingWithConsent extends Value('C')

  object TrackingOnlyIfConsented extends Value('P')

  object Disregarding extends Value('D')

  object Updated extends Value('U')

  def apply(string: String) =
    try {
      if (string.isEmpty) { throw Invalid }

      new TrackingStatus(string.charAt(0) match {
        case UnderConstruction.flag       => UnderConstruction
        case Dynamic.flag                 => Dynamic
        case Gateway.flag                 => Gateway
        case NotTracking.flag             => NotTracking
        case TrackingWithConsent.flag     => TrackingWithConsent
        case TrackingOnlyIfConsented.flag => TrackingOnlyIfConsented
        case Disregarding.flag            => Disregarding
        case Updated.flag                 => Updated
      })
    } catch {
      case _: IndexOutOfBoundsException => throw Invalid
    }

}
