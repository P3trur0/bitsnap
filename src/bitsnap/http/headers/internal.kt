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

package bitsnap.http.headers

import bitsnap.exceptions.InvalidHeaderException
import bitsnap.exceptions.HeaderDuplicateException
import bitsnap.exceptions.InvalidHeaderQualityException
import bitsnap.exceptions.InvalidQualityValueException
import bitsnap.http.toFloatOrThrow
import java.util.*

val acceptQualityComparator = Comparator<Pair<*, Int>> { one, other -> one.second.compareTo(other.second) * -1 } // reversed

internal fun <T> joinToStringWithQuality(values: List<Pair<T, Int>>, with: (T) -> String) = values.asSequence()
    .sortedWith(acceptQualityComparator)
    .joinToString(", ") {
        "${with(it.first)}${
        if (it.second < 10 && it.second > 0)
            ";q=0.${it.second}"
        else ""
        }"
    }

internal fun <T> joinToStringWithQuality(values: List<Pair<T, Int>>) = joinToStringWithQuality(values) { it.toString() }

@Throws(InvalidHeaderException::class)
internal fun String.splitValueQuality(): Sequence<Pair<String, Int>> = this.split(",").asSequence()
    .map { it.trim() }
    .map { it ->
        val qualityIndex = it.indexOfAny(listOf("q =", "q="))
        if (qualityIndex > 0) {
            val value = it.substring(0, qualityIndex).substringBefore(";")
            val qualityString = it.substring(qualityIndex).substringBefore(";")
                .removePrefix("q=")
                .removePrefix("q =")
                .trim()

            val quality = (
                qualityString.toFloatOrThrow(InvalidHeaderQualityException(qualityString))
                    * 10
                ).toInt()

            Pair(value.trim(), quality)
        } else {
            Pair(it, 10)
        }
    }

@Throws(InvalidQualityValueException::class)
internal inline fun checkQuality(headerName: String, quality: Int, block: () -> Unit) {
    if (quality !in 1..10) {
        throw InvalidQualityValueException(headerName, quality.toString())
    } else {
        block()
    }
}

@Throws(InvalidQualityValueException::class)
internal fun checkQuality(headerName: String, quality: Int) {
    if (quality !in 1..10) {
        throw InvalidQualityValueException(headerName, quality.toString())
    }
}

internal fun <T> List<T>.findDuplicates(compare: (T, T) -> Boolean) = this.filterIndexed { oneIdx, one ->
    this.filterIndexed { otherIdx, other ->
        compare(one, other) &&
            oneIdx != otherIdx &&
            otherIdx > oneIdx
    }.isNotEmpty()
}

internal fun <T> List<T>.findDuplicates() = findDuplicates() { one, other -> one == other }

internal fun <T> List<T>.checkHeaderDuplicates(name: String) {
    val duplicates = this.findDuplicates()

    if (duplicates.isNotEmpty()) {
        throw HeaderDuplicateException(name, duplicates.joinToString(", "))
    }
}

internal fun <T> List<Pair<T, *>>.checkHeaderPairDuplicates(name: String) {
    val duplicates = this.findDuplicates() { one, other -> one.first == other.first }

    if (duplicates.isNotEmpty()) {
        throw HeaderDuplicateException(name, duplicates.joinToString(", "))
    }
}
