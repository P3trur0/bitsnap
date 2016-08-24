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

import bitsnap.exceptions.InvalidMimeTypeException
import java.util.*

data class MimeType internal constructor(val type: String, val subType: String, val parameters: Map<String, String>) {

    constructor(type: String, subType: String) : this(type, subType, emptyMap())

    init {
        @Throws(InvalidMimeTypeException::class)
        if (!type.isValidParam()) {
            throw InvalidMimeTypeException("type $type")
        }

        @Throws(InvalidMimeTypeException::class)
        if (!subType.isValidParam()) {
            throw InvalidMimeTypeException("subtype $subType")
        }
    }

    fun typeToString() = "$type/$subType"

    fun parametersToString() = if (parameters.isNotEmpty()) "; ${
    parameters.entries.joinToString(
        separator = "; ",
        transform = { "${it.key}=${it.value}" })
    }" else ""

    override fun toString() = "${typeToString()}${parametersToString()}"

    override fun equals(other: Any?) = if (other is MimeType) {
        this.type == other.type && this.subType == other.subType
    } else false

    companion object {

        @Throws(InvalidMimeTypeException::class)
        operator fun invoke(mimeString: String): MimeType {
            val slashIndex = mimeString.indexOf('/')
            val semIndex = mimeString.indexOf(';')
            if (slashIndex > 0) {
                if (slashIndex > semIndex && semIndex > 0) throw InvalidMimeTypeException(mimeString)

                val type = mimeString.substring(0, slashIndex).toLowerCase(Locale.ENGLISH)

                val subtype = if (semIndex > 0) {
                    mimeString.substring(slashIndex + 1, semIndex).toLowerCase(Locale.ENGLISH)
                } else {
                    mimeString.substring(slashIndex + 1).toLowerCase(Locale.ENGLISH)
                }

                if (!type.isValidParam() || !subtype.isValidParam()) {
                    throw InvalidMimeTypeException(mimeString)
                }

                val parameters = if (semIndex > 0) mimeString.substring(semIndex + 1).splitParameters() {
                    InvalidMimeTypeException(it)
                } else {
                    emptyMap()
                }

                return MimeType(type, subtype, parameters)
            } else throw InvalidMimeTypeException(mimeString)
        }
    }
}
