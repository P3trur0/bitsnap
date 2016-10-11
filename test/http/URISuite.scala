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

import org.scalatest.{FlatSpec, Inspectors, Matchers, OptionValues}

object URISuite {
  val testUris = Seq(
    "http://user:pass@host:port/path?param1=1&param2=2+3#fragment",
    "http://user:pass@host:port/path#?fragment?"
  )
}

class URISuite extends FlatSpec with Matchers with OptionValues with Inspectors {
  "URI" should "be parsed" in {}
}
