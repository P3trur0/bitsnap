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

import java.util.*

data class MimeType internal constructor(val type: String, val subType: String, val parameters: Map<String, String>) {

    constructor(type: String, subType: String) : this(type, subType, emptyMap())

    class MimeTypeBuildException(message: String): HttpBuildException("MimeType $message is invalid")

    class MimeTypeParseException(type: String): HttpParseException("Cant parse MIME Type $type")

    init {
        @Throws(MimeTypeBuildException::class)
        if (!typeIsValid(type)) {
            throw MimeTypeBuildException("type $type")
        }

        @Throws(MimeTypeBuildException::class)
        if (!typeIsValid(subType)) {
            throw MimeTypeBuildException("subtype $subType")
        }
    }

    fun typeToString() = "$type/$subType"

    fun parametersToString() = if (parameters.isNotEmpty()) "; ${
        parameters.entries.joinToString(
            separator = "; ",
            transform = { "${it.key}=${it.value}" })
    }" else ""

    override fun toString() = "${typeToString()}${parametersToString()}"

    companion object {

        private val forbiddenTypeChars: String = "()<>@,;:/[]?=\\\""

        fun Char.isPrintable() = this.toInt() > 32 && this.toInt() < 127 // of space to del

        internal fun typeIsValid(type: String) =
            type.asSequence().find { !it.isPrintable() || forbiddenTypeChars.contains(it) } != null

        @Throws(MimeTypeParseException::class)
        fun from(mimeString: String) : MimeType {
            val slashIndex = mimeString.indexOf('/')
            val semIndex = mimeString.indexOf(';')
            if (slashIndex >= 0) {
                if (slashIndex > semIndex) throw MimeTypeParseException(mimeString)

                val type = mimeString.substring(0, slashIndex).toLowerCase(Locale.ENGLISH)

                val subtype = if (semIndex > 0) {
                    mimeString.substring(slashIndex, semIndex).toLowerCase(Locale.ENGLISH)
                } else {
                    mimeString.substring(slashIndex).toLowerCase(Locale.ENGLISH)
                }

                if (!typeIsValid(type) && !typeIsValid(subtype)) throw MimeTypeParseException(mimeString)

                val parameters = mimeString.substring(semIndex).splitParameters() {
                    MimeTypeParseException(it)
                }

                return MimeType(type, subtype, parameters)
            } else throw MimeTypeParseException(mimeString)
        }
    }
}
