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

class Connection private (val `type`: String) extends Header {
  override val name: String  = Connection.name
  override val value: String = `type`
}

object Connection {
  private[http] final val name = "connection"

  object KeepAlive extends Connection("keep-alive")
  object Close     extends Connection("close")
  object Upgrade   extends Connection("Upgrade")

  object Unknown extends Header.Invalid

  def apply(string: String): Try[Connection] = {
    string.toLowerCase match {
      case KeepAlive.`type` => Success(KeepAlive)
      case Close.`type`     => Success(Close)
      case Upgrade.`type`   => Success(Upgrade)
      case _                => Failure(Unknown)
    }
  }
}
