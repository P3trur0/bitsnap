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

final class Connection(val `type`: Connection.Type) extends Header {
  override val name: String  = Connection.name
  override val value: String = `type`.toString
}

object Connection {
  private[http] final val name = "connection"

  sealed abstract class Type(private[http] val name: String) {
    override def toString = name
  }

  object KeepAlive extends Type("keep-alive")
  object Close     extends Type("close")
  object Upgrade   extends Type("Upgrade")

  object Unknown extends Header.Invalid

  def apply(string: String) =
    new Connection(string match {
      case KeepAlive.name => KeepAlive
      case Close.name     => Close
      case Upgrade.name   => Upgrade
      case _              => throw Unknown
    })
}
