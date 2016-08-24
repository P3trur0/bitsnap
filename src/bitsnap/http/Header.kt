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

import bitsnap.exceptions.HeaderDateParseException
import bitsnap.exceptions.InvalidHeaderException
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

abstract class Header {

    internal interface HeaderCompanion {
        val name: String
        operator fun invoke(value: String): Header
    }

    abstract val name: String

    abstract val value: String

    override fun toString() = "$name: $value"

    companion object {

        fun formatDate(date: Date): String = dateFormat.format(date.toInstant().atZone(ZoneId.of("GMT")))

        fun parseDate(value: String): Date = try {
            Date.from(ZonedDateTime.parse(value, Header.dateFormat).toInstant())
        } catch (e: DateTimeParseException) {
            throw HeaderDateParseException(value)
        }

        internal val dateFormat = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneId.of("GMT"))

        internal val headerCompanions: MutableMap<String, HeaderCompanion> = HashMap()

        internal fun registerCompanion(companion: HeaderCompanion) {
            synchronized(headerCompanions) {
                headerCompanions.put(companion.name.toLowerCase(), companion)
            }
        }

        operator fun invoke(name: String, value: String) = headerCompanions[name.toLowerCase()]?.invoke(value) ?: Header.Unknown(name, value)

        @Throws(InvalidHeaderException::class)
        operator fun invoke(headerString: String): Header {
            val sepIndex = headerString.indexOf(": ")
            if (sepIndex < 0) {
                throw InvalidHeaderException(headerString)
            }

            val name = headerString.substring(0, sepIndex)
            val value = headerString.substring(sepIndex)

            return invoke(name, value)
        }
    }

    open val allowMultiple = false

    class Unknown(override val name: String, override val value: String) : Header()
}
