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

package bitsnap.http.headers

import bitsnap.exceptions.UnknownTrackingStatus
import bitsnap.http.Header

class DoNotTrack internal constructor(override val value: String) : Header() {

    override val name = DoNotTrack.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(DoNotTrack.Companion)
        }

        override val name = "DNT"

        override operator fun invoke(value: String) = DoNotTrack(value)
    }
}

class TrackingStatus internal constructor(statusValue: StatusValue) : Header() {

    override val name = TrackingStatus.Companion.name

    override val value by lazy {
        statusValue.toString()
    }

    enum class StatusValue(val flag: Char) {
        UNDER_CONSTRUCTION('!'),
        DYNAMIC('?'),
        GATEWAY('G'),
        NOT_TRACKING('N'),
        TRACKING('T'),
        TRACKING_IF_CONSENTED('C'),
        DISREGARDING_DNT('D'),
        UPDATED('U');

        override fun toString() = flag.toString()
    }

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(TrackingStatus.Companion)
        }

        override val name = "TSV"

        override operator fun invoke(value: String) =
            TrackingStatus(StatusValue.values().find { it.flag == value[0] } ?: throw UnknownTrackingStatus(value))
    }
}
