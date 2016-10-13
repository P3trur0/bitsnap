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
package util

import org.scalatest.{FlatSpec, Matchers}

object PrimesSuite {
  val testPrimes = Seq(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71)
}

class PrimesSuite extends FlatSpec with Matchers {
  "Prime numbers" should "be found" in {
    Primes.get(PrimesSuite.testPrimes.size) should equal(PrimesSuite.testPrimes)
    Primes.known should equal(PrimesSuite.testPrimes)
    Primes.next should equal(73)
    Primes.known.last should equal(73)
  }
}
