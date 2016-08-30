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

class Data(
    val paddingLength: Byte,
    val data: ByteArray,
    flags: List<Data.Flags>,
    streamId: Int
) : Frame(1 + data.size + paddingLength, 0x00, flags.map { it as Flag }, streamId) {

    sealed class Flags {
        object EndStream : Flag, Flags() {
            override val value = 0x01.toByte()
            override fun toString() = "END_STREAM"
        }

        object Padded : Flag, Flags() {
            override val value = 0x08.toByte()
            override fun toString() = "PADDED"
        }
    }

    override val payload by lazy {
        ByteArray(data.size + 1) {
            when {
                it == 0 -> paddingLength
                data.size < it -> 0
                else -> data[it]
            }
        }
    }

    companion object {
        internal operator fun invoke(data: ByteArray, streamId: Int, endStream: Boolean) {

        }

        operator fun invoke(data: ByteArray, streamId: Int) = invoke(data, streamId, false)

        fun end(data: ByteArray, streamId: Int)  = invoke(data, streamId, true)
    }
}
