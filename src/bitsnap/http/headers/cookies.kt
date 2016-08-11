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

class Cookie internal constructor(override val value: String) : Header() {

    override val name = Cookie.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(Cookie.Companion)
        }

        override val name = "Cookie"
        override fun from(value: String) = Cookie(value)
    }
}

class SetCookie internal constructor(override val value: String) : Header() {

    override val name = SetCookie.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(SetCookie.Companion)
        }

        override val name = "Set-Cookie"
        override fun from(value: String) = SetCookie(value)
    }
}
