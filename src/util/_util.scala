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

package object util {

  private[bitsnap] implicit class RichSeq[M, T <: Ordered[M]](val from: Seq[T]) extends AnyVal {
    def binarySearch(key: M): Option[T] = {
      var (low, high)       = (0, from.size - 1)
      var result: Option[T] = None

      while (low <= high && result.isEmpty) {
        val mid: Int = (low + high) >>> 1 // equivalent div 2
        val x        = from(mid)
        x.compare(key) match {
          case -1 => low = mid + 1
          case 1  => high = mid - 1
          case _  => result = Some(x)
        }
      }

      result
    }
  }

  private[bitsnap] implicit class RichString(val from: String) extends AnyVal {

    def substringBefore(pos: Int): Option[String] = {
      if (pos > from.length) {
        None
      } else {
        val s = from.substring(0, pos)
        if (s.isEmpty) { None } else { Some(s) }
      }
    }

    def substringBefore(char: Char): Option[String] = {
      val pos = from.indexOf(char)
      if (pos <= 0) {
        None
      } else {
        substringBefore(pos)
      }
    }

    def substringAfter(pos: Int): Option[String] = {
      if (pos > from.length) {
        None
      } else {
        val s = from.substring(pos + 1)
        if (s.isEmpty) { None } else { Some(s) }
      }
    }

    def substringAfter(char: Char): Option[String] = {
      val pos = from.indexOf(char)
      if (pos < 0) {
        None
      } else {
        substringAfter(pos)
      }
    }

    def splitNameValue = {
      val pos = from.indexOf('=')
      if (pos <= 0) {
        None
      } else {
        Some((from.substring(0, pos), from.substring(pos + 1)))
      }
    }

    def containsAny(seq: Seq[Char]) = from.exists { seq.contains(_) }

    def containsAny(seq: String) = from.exists { seq.contains(_) }

    def hasQuotes = from match {
      case _ if from == "\"\"" || from == "\'\'" || from == "\"" || from == "\'"                     => true
      case _ if from.length < 2                                                                      => false
      case _ if (from.head == '\"' || from.head == '\'') && (from.last == '\"' || from.last == '\'') => true
      case _                                                                                         => false
    }

    def stripPrefixIgnoreCase(prefix: String) = {
      from.stripPrefix(prefix).stripPrefix(prefix.toLowerCase)
    }

    def stripQuotes = {
      if (from == "\'" || from == "\"") {
        ""
      } else if (hasQuotes) {
        val (s, e) = (
          if (from.head == '\"' || from.head == '\'') { 1 } else { 0 },
          if (from.last == '\"' || from.last == '\'') { from.length - 1 } else { from.length }
        )

        from.substring(s, e)
      } else {
        from
      }
    }

    def quoted = "\"" ++ from ++ "\""
  }
}
