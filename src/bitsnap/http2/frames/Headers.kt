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

package bitsnap.http2.frames

import bitsnap.http2.Frame

class Headers(
    val paddingLength: Byte,
    streamId: Int,
    val streamDependency: Int ?,
    val weight: Byte ?,
    flags: List<Headers.Flags>,
    val headers: ByteArray
) : Frame(1 + // padding length
    if (streamDependency != null) 4 else 1 +
    if (weight != null) 1 else 0 +
        headers.size +
        paddingLength,
    0x01,
    flags.map { it as Flag },
    streamId
    ) {

    override val payload by lazy {
        ByteArray(headers.size + 1) {
            when {
                it == 0 -> paddingLength
                headers.size < it -> 0
                else -> headers[it]
            }
        }
    }

    sealed class Flags {
        object EndStream : Flag, Flags() {
            override val value = 0x01.toByte()
            override fun toString() = "END_STREAM"
        }

        object EndHeaders: Flag, Flags() {
            override val value = 0x01.toByte()
            override fun toString() = "END_HEADERS"
        }

        object Padded : Flag, Flags() {
            override val value = 0x08.toByte()
            override fun toString() = "PADDED"
        }

        object Priority: Flag, Flags() {
            override val value = 0x20.toByte()
            override fun toString() = "PRIORITY"
        }
    }
}
