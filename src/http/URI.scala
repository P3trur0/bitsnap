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

final class URI(val schema: Option[String],
                val authority: Option[URI.Authority],
                val path: String,
                val query: Option[URI.Query],
                val fragment: Option[URI.Fragment]) {

  if ((authority.isDefined && schema.isEmpty) || path.isEmpty) {
    throw URI.Invalid
  }

  def host = authority match {
    case Some(x) => x.host
    case _ => None
  }

  def port = authority match {
    case Some(x) => x.port
    case _ => None
  }


}

object URI {

  object Invalid extends Http.Invalid

  private[http] val schemaRegex = """[A-Za-z0-9\-\._~]+""".r

  private[http] def isValidSchema(name: String) = schemaRegex.pattern.matcher(name).matches() && name.head.isLetter

  private[http] val generalDelimiters = Seq(':', '/', '?', '#', '[', ']', '@').map { c =>
    c -> s"%${c.toInt}"
  }.toMap

  private[http] val subDelimiters = Seq('!', '$', '&', '\'', '(', ')', '*', '+', ',', ';', '=', ' ').map { c =>
    c -> s"%${c.toInt}"
  }.toMap

  final class Authority(val host: String, val port : Option[Int], val userInfo: Option[String]) {

  }

  final class Query {

  }

  final class Fragment {

  }
}
