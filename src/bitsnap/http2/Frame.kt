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

package bitsnap.http2


abstract class Frame(
    val length: Int, // 3
    val type: Byte, // 1
    val flags: List<Frame.Flag>, // 1
    val streamId: Int // 4 (first bit ignored)
) {
    abstract val payload: ByteArray

    interface Flag {
        val value: Byte


    }
}
