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

private[http] sealed class Method(private val name: String) extends Ordered[String] {
  override def toString              = name
  override def compare(that: String) = name compare that
}

object Method {
  case object OPTIONS extends Method("OPTIONS")
  case object GET     extends Method("GET")
  case object HEAD    extends Method("HEAD")
  case object PUT     extends Method("PUT")
  case object POST    extends Method("POST")
  case object DELETE  extends Method("DELETE")
  case object TRACE   extends Method("TRACE")
  case object CONNECT extends Method("CONNECT")

  object Unknown extends Header.Invalid

  private[http] val known = List(
    OPTIONS,
    GET,
    HEAD,
    PUT,
    POST,
    DELETE,
    TRACE,
    CONNECT
  ).sorted(new Ordering[Method] {
    override def compare(a: Method, b: Method): Int = a.compare(b.name)
  })

  def apply(name: String): Try[Method] =
    if (name.isEmpty) {
      Failure(Unknown)
    } else {
      known.find { e =>
        e.name(0) == name(0) && e.name(1) == name(1)
      } match {
        case Some(method) => Success(method)
        case None         => Failure(Unknown)
      }
    }

  def unapplySeq(string: String): Option[Seq[Method]] = {
    val methods = string
      .split(",")
      .map { s =>
        Method(s.trim)
      }
      .filter { _.isSuccess }
      .map { _.get }

    if (methods.isEmpty) {
      None
    } else {
      Some(methods)
    }
  }
}
