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

data class MimeType internal constructor(val type: String, val subType: String, val parameters: List<Parameter>) {

    constructor(type: String, subType: String, vararg parameters: Parameter) : this(type, subType, parameters.asList())

    constructor(type: String, subType: String) : this(type, subType, emptyList())

    class MimeTypeBuildException(message: String): HttpBuildException("MimeType $message is invalid")

    class MimeTypeParseException(type: String): HttpParseException("Cant parse MIME Type $type")

    class MimeTypeCastException(name: String, type: String): HttpParseException("Can't cast $name parameter to $type")

    init {
        if (!typeIsValid(type)) {
            throw MimeTypeBuildException("type $type")
        }

        if (!typeIsValid(subType)) {
            throw MimeTypeBuildException("subtype $subType")
        }
    }

    override fun toString() = "$type/$subType;${parameters.joinToString("; ")}"

    abstract class Parameter(val name: String) {

        abstract fun value(): String

        override fun toString() = "$name=${value()}"

        class FloatParameter internal constructor(name: String, val value: Float) : Parameter(name) {
            override fun value() = value.toString()
        }

        class IntParameter internal constructor(name: String, val value: Int) : Parameter(name) {
            override fun value() = value.toString()
        }

        class StringParameter internal constructor(name: String, val value: String) : Parameter(name) {
            override fun value() = value

            fun toIntParameter() : IntParameter {
                try {
                    return IntParameter(name, value.toInt())
                } catch (e: NumberFormatException) {
                    throw MimeTypeCastException(name, "Int")
                }
            }

            fun toFloatParameter() : FloatParameter {
                try {
                    return FloatParameter(name, value.toFloat())
                } catch (e: NumberFormatException) {
                    throw MimeTypeCastException(name, "Float")
                }
            }
        }

        class ParameterParseException(message: String): HttpParseException(message)

        companion object {
            private fun nameValueFrom(mimeString: String) : Pair<String, String> {
                if (mimeString.indexOf('=') < 0) throw ParameterParseException("Can't parse $mimeString MimeType Parameter")
                val chunks = mimeString.split("=")
                if (chunks.size != 2) throw ParameterParseException("$mimeString is not a MimeType Parameter")
                return Pair(chunks[0].trim(), chunks[1].trim())
            }

            fun floatParameterFrom(mimeString: String) : FloatParameter {
                val (name, value) = nameValueFrom(mimeString)
                try {
                    return FloatParameter(name, value.toFloat())
                } catch(e: NumberFormatException) {
                    throw ParameterParseException("$mimeString is not a MimeType Float Parameter")
                }
            }

            fun intParameterFrom(mimeString: String) : IntParameter {
                val (name, value) = nameValueFrom(mimeString)
                try {
                    return IntParameter(name, value.toInt())
                } catch(e: NumberFormatException) {
                    throw ParameterParseException("$mimeString is not a MimeType Int Parameter")
                }
            }

            fun stringParameterFrom(mimeString: String) : StringParameter {
                val (name, value) = nameValueFrom(mimeString)
                return StringParameter(name, value)
            }

            fun from(mimeString: String) = stringParameterFrom(mimeString)
        }
    }

    companion object {

        private val forbiddenTypeChars: String = "()<>@,;:/[]?=\\\""

        fun Char.isPrintable() = this.toInt() > 32 && this.toInt() < 127 // of space to del

        internal fun typeIsValid(type: String) =
            type.asSequence().find { !it.isPrintable() || forbiddenTypeChars.contains(it) } != null

        internal fun parseParameters(string: String) =
            string.split(";").filter { it.isNotEmpty() }.map { Parameter.from(it) }

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

                return MimeType(type, subtype, parseParameters(mimeString.substring(semIndex)))
            } else throw MimeTypeParseException(mimeString)
        }
    }
}
