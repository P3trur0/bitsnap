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

import util._

final class URI(val schema: Option[URI.Schema],
                val authority: Option[URI.Authority],
                val path: String,
                val query: Option[URI.Query],
                val fragment: Option[String]) {

  if ((authority.isDefined && schema.isEmpty) || path.isEmpty) {
    throw URI.Invalid
  }

  def host = authority.map { _.host }

  def port = authority.flatMap { _.port }

  def username = authority.map { _.userInfo.map { _.username } }

  def password = authority.map { _.userInfo.flatMap { _.password } }

  def get(string: String) = query.flatMap { _.values.get(string) }

  override def equals(that: Any) = that match {
    case that: URI =>
      this.schema == that.schema &&
        this.authority == that.authority &&
        this.path == that.path &&
        this.query == that.query &&
        this.fragment == that.fragment
    case _ => false
  }

  override def hashCode =
    http.hashCodePrime +
      schema.map { _.hashCode }.getOrElse(0) +
      authority.map { _.hashCode }.getOrElse(0) +
      path.hashCode +
      query.map { _.hashCode }.getOrElse(0) +
      fragment.map { _.hashCode }.getOrElse(0)
}

object URI {

  object Invalid extends Http.Invalid

  private[http] val schemaRegex = """[A-Za-z0-9\-\._~]+""".r

  private[http] def isValidSchema(name: String) =
    schemaRegex.pattern.matcher(name).matches() &&
      name.headOption.nonEmpty &&
      name.head.isLetter

  private[http] val delimiters = Seq(
    ' ',
    ':',
    '/',
    '?',
    '#',
    '[',
    ']',
    '@',
    '!',
    '$',
    '&',
    '\'',
    '\"',
    '(',
    ')',
    '*',
    '+',
    ',',
    ';',
    '=',
    '%',
    '-',
    '.',
    '>',
    '<',
    '\\',
    '^',
    '_',
    '`',
    '{',
    '|',
    '}',
    '~'
  )

  private[http] val delimitersPct = delimiters.map { c =>
    c -> s"%${c.toString.getBytes(Charset.ASCII.charset).head}"
  }.toMap

  def encode(string: String): String =
    string.map { c =>
      delimitersPct.getOrElse(c, c)
    }.mkString("")

  def decode(string: String): String = {

    var chunks = Seq(string)

    delimitersPct.foreach { e =>
      chunks = chunks.flatMap {
        _.split(e._2).flatMap { List(_, e._1.toString) }.dropRight(1)
      }
    }

    chunks.mkString("")
  }

  class Schema(val name: String) {
    override def equals(that: Any) = that match {
      case that: Schema => this.name == that.name
      case _            => false
    }

    override def hashCode = http.hashCodePrime + name.hashCode
  }

  object Schema {
    object Http  extends Schema("http")
    object Https extends Schema("https")
    object WS    extends Schema("ws")
  }

  final class Authority(val host: String, val port: Option[Int], val userInfo: Option[Authority.UserInfo]) {
    if (host.containsAny(delimiters)) {
      throw URI.Invalid
    }

    override def equals(that: Any) = that match {
      case that: Authority => this.host == that.host && this.port == that.port && this.userInfo == that.userInfo
      case _               => false
    }

    override def hashCode =
      http.hashCodePrime + host.hashCode + this.port.getOrElse(0) + userInfo.map { _.hashCode }.getOrElse(0)
  }

  object Authority {
    final class UserInfo(val username: String, val password: Option[String]) {
      if (username.containsAny(delimiters) || password.exists { _.containsAny(delimiters) }) {
        throw URI.Invalid
      }

      def length = if (password.nonEmpty) { password.get.length + 1 } else { 0 } + username.length

      override def equals(that: Any) = that match {
        case that: UserInfo => this.username == that.username && this.password == that.password
        case _              => false
      }

      override def hashCode = http.hashCodePrime + username.hashCode + this.password.getOrElse("").hashCode
    }

    object UserInfo {
      def apply(string: String): UserInfo = {
        val (username, password) = string.substringBefore(':') match {
          case Some(u) => (u, string.substringAfter(':'))
          case _       => (string, None)
        }

        new UserInfo(username, password)
      }
    }
  }

  implicit class Query private[http] (val values: Map[String, String]) extends AnyVal {
    override def toString =
      values.map { e =>
        s"${e._1}=${e._2.replace(' ', '+')}"
      }.mkString("&")

//    override def equals(that: Any) = that match {
//      case that: Query => this.values == that.values
//      case _ => false
//    }
//
//    override def hashCode = http.hashCodePrime + (0 /: values.map( e => e._1.hashCode + e._2.hashCode)) { _ + _ }
  }

  object Query {
    def apply(string: String): Query = {
      Query(
        string
          .split('&')
          .map { s =>
            val eqIdx = s.indexOf('=')
            if (eqIdx <= 0) {
              None
            } else {
              val (n, v) = (s.substringBefore(eqIdx), s.substringAfter(eqIdx))
              if (n.isEmpty || v.isEmpty) {
                None
              } else {
                Some(n.get, v.get.replace('+', ' '))
              }
            }
          }
          .filter { _.isDefined }
          .map { _.get }
          .toMap)
    }
  }

  private[http] def splitHostPort(string: String): (String, Option[Int]) = {
    val hp = string.substringBefore('/').getOrElse(string)
    try {
      val semPos = hp.indexOf(':')
      if (semPos > 0) {
        (hp.substringBefore(semPos).get, hp.substringAfter(semPos).map { _.toInt })
      } else { (hp, None) }
    } catch {
      case _: NumberFormatException => throw Invalid
    }
  }

  private[http] def splitPathFragmentQuery(string: String): (String, Option[Query], Option[String]) = {
    val qIdx  = string.indexOf('?')
    val ocIdx = string.indexOf('#')

    val path = if (qIdx >= 0) {
      string.substringBefore(qIdx).getOrElse("/")
    } else {
      if (ocIdx >= 0) {
        string.substringBefore(ocIdx).getOrElse("/")
      } else {
        string
      }
    }

    val query = if (qIdx >= 0) {
      ocIdx match {
        case _ if ocIdx >= 0 && ocIdx > qIdx => Some(Query(string.substring(qIdx, ocIdx)))
        case _ if ocIdx >= 0 && ocIdx < qIdx => None
        case _                               => Some(Query(string.substring(qIdx)))
      }
    } else {
      None
    }

    val fragment = if (ocIdx >= 0) {
      string.substringAfter(ocIdx)
    } else {
      None
    }

    (path, query, fragment)
  }

  def apply(string: String) = {
    var pos = 0

    val schema = string match {
      case _ if string.startsWith("http://") => {
        pos += 7
        Some(Schema.Http)
      }
      case _ if string.startsWith("https://") => {
        pos += 8
        Some(Schema.Https)
      }
      case _ if string.startsWith("ws://") => {
        pos += 5
        Some(Schema.WS)
      }

      case _ =>
        string.substringBefore(':') match {
          case Some(name) if isValidSchema(name) => {
            pos += name.length + 3
            Some(new Schema(name))
          }
          case _ => None
        }
    }

    if (pos > string.length ||
        string.charAt(pos - 1) != '/' ||
        string.charAt(pos - 2) != '/') {
      throw Invalid
    }

    val userInfo: Option[Authority.UserInfo] = string.substring(pos).substringBefore('@').map { i =>
      pos += i.length
      if (i.containsAny("/#?")) {
        throw Invalid
      }

      Authority.UserInfo(i)
    }

    val hostString = string.substring(pos)
    pos += hostString.length
    val (host, port) = splitHostPort(hostString)

    val (path, query, fragment) = if (pos == string.length || pos + 1 == string.length) {
      ("/", None, None)
    } else {
      splitPathFragmentQuery(string.substring(pos))
    }

    val authority: Option[Authority] = if (host.isEmpty && port.isEmpty && userInfo.isEmpty) {
      None
    } else {
      Some(
        new Authority(
          host,
          port,
          userInfo
        ))
    }

    new URI(schema, authority, path, query, fragment)
  }
}
