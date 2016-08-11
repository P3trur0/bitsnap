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

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*
import java.util.concurrent.ConcurrentHashMap

abstract class Header {

    open class HeaderParseException(message: String) : HttpParseException(message)

    class HeaderDateParseException(value: String) : HeaderParseException("Can't parse Date $value")

    internal interface HeaderCompanion {
        val name: String

        fun from(value: String): Header

        fun formatDate(date: Date) : String = dateFormat.format(date.toInstant().atZone(ZoneId.of("GMT")))

        fun parseDate(value: String) : Date = try {
            Date.from(ZonedDateTime.parse(value, Header.dateFormat).toInstant())
        } catch (e: DateTimeParseException) {
            throw Header.HeaderDateParseException(value)
        }
    }

    abstract val name: String

    abstract val value: String

    override fun toString() = "$name: $value"

    companion object {

        internal val dateFormat = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneId.of("GMT"))

        internal val headerCompanions: MutableMap<String, HeaderCompanion> = ConcurrentHashMap()

        internal fun registerCompanion(companion: HeaderCompanion) {
            headerCompanions.put(companion.name, companion)
        }

        fun from(name: String, value: String) = headerCompanions[name]?.from(value) ?: Header.Unknown(name, value)

        @Throws(HeaderParseException::class)
        fun from(headerString: String) : Header {
            val sepIndex = headerString.indexOf(": ")
            if (sepIndex < 0) {
                throw HeaderParseException("Can't parse header $headerString")
            }

            val name = headerString.substring(0, sepIndex)
            val value = headerString.substring(sepIndex)

            return from(name, value)
        }
    }

    open val allowMultiple = false

    class Unknown(override val name: String, override val value: String) : Header()
}
