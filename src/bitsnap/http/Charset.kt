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

import bitsnap.exceptions.UnknownCharsetException
import java.nio.charset.Charset as NioCharset

enum class Charset(val nioCharset: NioCharset, name: String) {
    ASCII(Charsets.US_ASCII, "ASCII"),
    ANSI(NioCharset.forName("CP1252"), "windows-1252"),
    ISO8859(Charsets.ISO_8859_1, "ISO-8859-1"),
    UTF8(Charsets.UTF_8, "UTF-8"),
    UTF16(Charsets.UTF_16, "UTF-16"),
    UTF32(Charsets.UTF_32, "UTF-32");


    override fun toString() = name

    companion object {

        operator fun invoke(charsetName: String): Charset {
            val lowercaseCharsetName = charsetName.toLowerCase()
            val possibleUTFCharsetName = lowercaseCharsetName.replace("-", "")

            return when (possibleUTFCharsetName) {
                "utf8" -> UTF8
                "utf16" -> UTF16
                "utf32" -> UTF32

                else -> null
            } ?: when (lowercaseCharsetName) {
                "ascii" -> ASCII
                "ansi" -> ANSI

                else -> null
            } ?: when {
                charsetName.contains("1252") -> ANSI
                charsetName.contains("8859") -> ISO8859

                else -> throw UnknownCharsetException(charsetName)
            }
        }
    }
}
