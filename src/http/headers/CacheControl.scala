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

import scala.util.{Failure, Success, Try}

final class CacheControl(val directives: Seq[CacheControl.Directive]) extends Header {

  if (directives.isEmpty) {
    throw CacheControl.Invalid
  }

  override val name: String = CacheControl.name

  override lazy val value: String = directives.mkString("", ", ", "")
}

object CacheControl {

  sealed abstract class Directive(val name: String) {

    val value: String

    if (name.isEmpty) {
      throw Directive.Invalid
    }

    override def toString =
      if (value.isEmpty) {
        name
      } else {
        s"$name=$value"
      }
  }

  private[http] abstract class PlainDirective(name: String) extends Directive(name) {
    override final val value: String = ""
  }

  private[http] abstract class AgeDirective(name: String) extends Directive(name) {

    val seconds: Int

    if (seconds <= 0) {
      throw Directive.Invalid
    }

    override val value: String = seconds.toString
  }

  object Directive {

    object Invalid extends Header.Invalid

    case class NoCache(override val value: String) extends Directive("no-cache")

    object NoCache extends NoCache("")

    case class Private(override val value: String) extends Directive("private")

    object Private extends Private("")

    case class MaxAge(override val seconds: Int)   extends AgeDirective("max-age")
    case class SMaxAge(override val seconds: Int)  extends AgeDirective("s-maxage")
    case class MaxStale(override val seconds: Int) extends AgeDirective("max-stale")
    case class MinFresh(override val seconds: Int) extends AgeDirective("min-fresh")

    object Public extends PlainDirective("public")

    object NoStore extends PlainDirective("no-store")

    object NoTransform extends PlainDirective("no-transform")

    object OnlyIfCached extends PlainDirective("only-if-cached")

    object MustRevalidate extends PlainDirective("must-revalidate")

    object ProxyRevalidate extends PlainDirective("proxy-revalidate")

    def apply(string: String): Try[Directive] = {
      string match {
        case NoCache.name                         => Success(NoCache)
        case _ if string.startsWith("no-cache=")  => Success(NoCache(string.stripPrefix("no-cache=")))
        case Private.name                         => Success(Private)
        case _ if string.startsWith("private=")   => Success(Private(string.stripPrefix("private=")))
        case _ if string.startsWith("max-age=")   => Success(MaxAge(string.stripPrefix("max-age=").toInt))
        case _ if string.startsWith("s-maxage=")  => Success(SMaxAge(string.stripPrefix("s-maxage=").toInt))
        case _ if string.startsWith("max-stale=") => Success(MaxStale(string.stripPrefix("max-stale=").toInt))
        case _ if string.startsWith("min-fresh=") => Success(MinFresh(string.stripPrefix("min-fresh=").toInt))

        case Public.name          => Success(Public)
        case NoStore.name         => Success(NoStore)
        case NoTransform.name     => Success(NoTransform)
        case OnlyIfCached.name    => Success(OnlyIfCached)
        case MustRevalidate.name  => Success(MustRevalidate)
        case ProxyRevalidate.name => Success(ProxyRevalidate)

        case _ =>
          val eqIdx = string.indexOf('=')
          if (eqIdx > 0) {
            val (n, v) = (string.substring(0, eqIdx), string.substring(eqIdx + 1))
            Success(new Directive(n) {
              override lazy val value = v
            })
          } else {
            Failure(Invalid)
          }
      }
    }
  }

  private[http] final val name = "cache-control"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String): Try[CacheControl] = {
    if (string.isEmpty) {
      Failure(Invalid)
    } else {

      val directives = string.split(',').map { _.trim }.map { Directive(_) }.filter { _.isSuccess }.map { _.get }
      directives.isEmpty match {
        case true => Failure(Invalid)
        case _    => Success(new CacheControl(directives))
      }
    }
  }
}
