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

import scala.collection.mutable
object Primes {

  val known = mutable.ArrayBuffer[Int](2)

  def isPrime(i: Int) = known.takeWhile(_ <= math.sqrt(i).toInt).forall(i % _ != 0)

  def next(): Int = known.synchronized {
    var r = known.last + 1
    while (!isPrime(r)) {
      r += 1
    }

    known += r
    r
  }

  def get(num: Int): Seq[Int] = {
    if (num > known.size) {
      known.size.until(num).foreach { _ =>
        next()
      }
    }

    known.slice(0, num)
  }
}
