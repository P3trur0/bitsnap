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

import io.bitsnap.http.Header.Implicit._

final class Link(val rel: Parameters) extends Header {
  override val name = Link.name

  override val value = rel.toString

  override def equals(that: Any) = that match {
    case that: Link => this.rel == that.rel
    case _          => false
  }

  override def hashCode = headers.hashCodePrime + rel.hashCode
}

object Link {

  sealed abstract class Relationship(final val name: String) {
    override def toString = s"rel=$name"
  }

  object Alternate extends Relationship("alternate")

  object Author extends Relationship("author")

  object DnsPrefetch extends Relationship("dns-prefetch")

  object Help extends Relationship("help")

  object Icon extends Relationship("icon")

  object License extends Relationship("license")

  object Next extends Relationship("next")

  object PingBack extends Relationship("pingback")

  object PreConnect extends Relationship("preconnect")

  object PreFetch extends Relationship("prefetch")

  object PreLoad extends Relationship("preload")

  object PreRender extends Relationship("prerender")

  object Prev extends Relationship("prev")

  object Search extends Relationship("search")

  object Stylesheet extends Relationship("stylesheet")

  object Relationship {

    object Unknown extends Header.Invalid

    def apply(name: String) = name match {
      case Alternate.name   => Alternate
      case Author.name      => Author
      case DnsPrefetch.name => DnsPrefetch
      case Help.name        => Help
      case Icon.name        => Icon
      case License.name     => License
      case Next.name        => Next
      case PingBack.name    => PingBack
      case PreConnect.name  => PreConnect
      case PreFetch.name    => PreFetch
      case PreLoad.name     => PreLoad
      case PreRender.name   => PreRender
      case Prev.name        => Prev
      case Search.name      => Search
      case Stylesheet.name  => Stylesheet
      case _                => throw Unknown
    }
  }

  private[http] final val name = "link"

  override def toString = name

  object Invalid extends Header.Invalid

  def apply(string: String) = {

    if (string.trim.isEmpty) {
      throw Invalid
    }

    val Parameters(parameters @ _ *) = string

    val rel = parameters.map { e =>
      new Header.Parameter(e.name, Map("rel" -> e.attributes.getOrElse("rel", { throw Invalid })))
    }

    if (rel.isEmpty) {
      throw Invalid
    } else {
      new Link(rel)
    }
  }
}
