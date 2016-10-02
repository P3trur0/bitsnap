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

import scala.language.implicitConversions

private[http] sealed abstract class Http(val version: String,
                                         val mimeType: MimeType,
                                         val headers: Headers,
                                         val body: Array[Byte],
                                         val charset: Charset) {

  def asJson = ""
}

object Http {

  class Invalid extends Exception

  class Request(val method: Method,
                val url: String,
                mimeType: MimeType,
                headers: Headers,
                body: Array[Byte],
                charset: Charset)
      extends Http("1.0", mimeType, headers, body, charset) {}

  class Response(val status: Status, mimeType: MimeType, headers: Headers, body: Array[Byte], charset: Charset)
      extends Http("1.0", mimeType, headers, body, charset) {}
}
