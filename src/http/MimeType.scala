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

final class MimeType(private[http] val mimeType: String, private[http] val mimeSubType: String)
    extends MimeType.Checks {
  override def toString: String = s"$mimeType/$mimeSubType"
}

object MimeType {

  private[http] trait Checks { this: MimeType =>

    final def isText        = mimeType == "text"
    final def isMultipart   = mimeType == "multipart"
    final def isMessage     = mimeType == "message"
    final def isImage       = mimeType == "image"
    final def isAudio       = mimeType == "audio"
    final def isVideo       = mimeType == "video"
    final def isModel       = mimeType == "model"
    final def isApplication = mimeType == "application"

    final def isJson  = isApplication && mimeSubType == "json"
    final def isJsonp = isApplication && mimeSubType == "javascript"
    final def isXml   = isApplication && mimeSubType == "xml"
  }

  object Invalid extends Header.Invalid

  def apply(string: String) = {
    try {
      val chunks = string.split("/")
      val (mimeType, mimeSubtype) = (
        chunks.headOption.getOrElse { throw Invalid },
        chunks.lastOption.getOrElse { throw Invalid }
      )

      new MimeType(mimeType, mimeSubtype)
    } catch {
      case e: NoSuchElementException => throw Invalid
    }
  }
}
