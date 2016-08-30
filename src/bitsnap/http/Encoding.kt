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

import bitsnap.exceptions.UnknownEncodingException

enum class Encoding(val type: String) {
    // Any encoding
    ANY("*"),
    COMPRESS("compress"), // Deprecated compress
    DEFLATE("deflate"), // Old Plain Deflate
    EXI("exi"), // W3C Efficient XML Interchange
    GZIP("gzip"),
    IDENTITY("identity"), // No transformation
    PACK200_GZIP("pack200-gzip"), // Network Transfer Format for Java Archives
    BR("br"), // Brotli
    BZIP2("bzip2"),
    LZMA("lzma"),
    PEERDIST("peerdist"),
    SDCH("sdch"),
    XPRESS("xpress"),
    XZ("xz");

    override fun toString() = type

    companion object {
        @Throws(UnknownEncodingException::class)
        operator fun invoke(value: String) = Encoding.values().find { it.type == value }
            ?: throw UnknownEncodingException(value)
    }
}
