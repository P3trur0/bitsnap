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

package bitsnap.http

import bitsnap.exceptions.InvalidRangeValue

sealed class Range {

    abstract val size: Int?

    protected fun sizeToString() = if (size != null) "/$size" else "/*"

    object None : Range() {
        override val size = null
        override fun toString() = ""
    }

    class Bounded(val range: IntRange, override val size: Int?) : Range() {

        init {
            val boundedSize = range.last - range.first + 1
            if (boundedSize <= 0) {
                throw InvalidRangeValue(this.toString())
            }

            if (size != null && boundedSize > size) {
                throw InvalidRangeValue(this.toString())
            }
        }

        override fun equals(other: Any?) = if (other is Bounded) {
            range == other.range && size == other.size
        } else false

        override fun toString() = "${range.first}-${range.last}${sizeToString()}"
    }

    class Head(val prefixEnd: Int, override val size: Int?): Range() {

        init {
            if (size != null && size < prefixEnd) {
                throw InvalidRangeValue(this.toString())
            }
        }

        override fun equals(other: Any?) = if (other is Head) {
            prefixEnd == other.prefixEnd && size == other.size
        } else false

        override fun toString() = "-$prefixEnd${sizeToString()}"
    }

    class Tail(val suffixStart: Int, override val size: Int?) : Range() {

        init {
            if (size != null && size < suffixStart) {
                throw InvalidRangeValue(this.toString())
            }
        }

        override fun equals(other: Any?) = if (other is Tail) {
            suffixStart == other.suffixStart && size == other.size
        } else false

        override fun toString() = "$suffixStart-${sizeToString()}"
    }

    companion object {
        operator fun invoke(value: String): Range {

            val chunks = value.split('/')
            val rangeString = chunks[0]

            val size = if (chunks.size == 2) {
                chunks[1].toIntOrNull()
            } else {
                null
            }

            return when {
                rangeString.startsWith('-') -> Head(rangeString.removePrefix("-")
                    .toIntOrThrow(InvalidRangeValue(value)), size)
                rangeString.endsWith('-') -> Tail(rangeString.removeSuffix("-")
                    .toIntOrThrow(InvalidRangeValue(value)), size)
                else -> {
                    val chunks = rangeString.split('-').map { it.toIntOrThrow(InvalidRangeValue(value)) }
                    if (chunks.size != 2) {
                        throw InvalidRangeValue(value)
                    }

                    Bounded(IntRange(chunks[0], chunks[1]), size)
                }
            }
        }
    }
}
