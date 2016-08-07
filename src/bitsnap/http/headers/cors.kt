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

class AccessControlAllowOrigin internal constructor(override val value: String) : Header() {

    override val name = AccessControlAllowOrigin.Companion.name

    companion object : HeaderCompanion {
        override val name = "Access-Control-Allow-Origin"
        override fun from(value: String) = AccessControlAllowOrigin(value)
    }
}

class AccessControlRequestMethod internal constructor(override val value: String) : Header() {

    override val name = AccessControlRequestMethod.Companion.name

    companion object : HeaderCompanion {
        override val name = "Access-Control-Request-Method"
        override fun from(value: String) = AccessControlRequestMethod(value)
    }
}

class AccessControlRequestHeaders internal constructor(override val value: String) : Header() {

    override val name = AccessControlRequestHeaders.Companion.name

    companion object : HeaderCompanion {
        override val name = "Access-Control-Request-Headers"
        override fun from(value: String) = AccessControlRequestHeaders(value)
    }
}

class AccessControlAllowCredentials internal constructor(override val value: String) : Header() {

    override val name = AccessControlAllowCredentials.Companion.name

    companion object : HeaderCompanion {
        override val name = "Access-Control-Allow-Credentials"
        override fun from(value: String) = AccessControlAllowCredentials(value)
    }
}

class AccessControlExposeHeaders internal constructor(override val value: String) : Header() {

    override val name = AccessControlExposeHeaders.Companion.name

    companion object : HeaderCompanion {
        override val name = "Access-Control-Expose-Headers"
        override fun from(value: String) = AccessControlExposeHeaders(value)
    }
}

class AccessControlMaxAge internal constructor(override val value: String) : Header() {

    override val name = AccessControlMaxAge.Companion.name

    companion object : HeaderCompanion {
        override val name = "Access-Control-Max-Age"
        override fun from(value: String) = AccessControlMaxAge(value)
    }
}

class AccessControlAllowMethods internal constructor(override val value: String) : Header() {

    override val name = AccessControlAllowMethods.Companion.name

    companion object : HeaderCompanion {
        override val name = "Access-Control-Allow-Methods"
        override fun from(value: String) = AccessControlAllowMethods(value)
    }
}

class AccessControlAllowHeaders internal constructor(override val value: String) : Header() {

    override val name = AccessControlAllowHeaders.Companion.name

    companion object : HeaderCompanion {
        override val name = "Access-Control-Allow-Headers"
        override fun from(value: String) = AccessControlAllowHeaders(value)
    }
}


