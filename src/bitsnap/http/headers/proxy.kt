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

import bitsnap.http.Header

class Forwarded internal constructor(override val value: String) : Header() {

    override val name = Forwarded.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(Forwarded.Companion)
        }

        override val name = "Forwarded"
        override fun from(value: String) = Forwarded(value)
    }
}

class MaxForwards internal constructor(override val value: String) : Header() {

    override val name = MaxForwards.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(MaxForwards.Companion)
        }

        override val name = "Max-Forwards"
        override fun from(value: String) = MaxForwards(value)
    }
}

class Via internal constructor(override val value: String) : Header() {

    override val name = Via.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(Via.Companion)
        }

        override val name = "Via"
        override fun from(value: String) = Via(value)
    }
}

class ProxyAuthenticate internal constructor(override val value: String) : Header() {

    override val name = ProxyAuthenticate.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(ProxyAuthenticate.Companion)
        }

        override val name = "Proxy-Authenticate"
        override fun from(value: String) = ProxyAuthenticate(value)
    }
}
