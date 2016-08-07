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

class XFrameOptions internal constructor(override val value: String) : Header() {

    override val name = XFrameOptions.Companion.name

    companion object : HeaderCompanion {
        override val name = "X-Frame-Options"
        override fun from(value: String) = XFrameOptions(value)
    }
}

class XContentSecurityPolicy internal constructor(override val value: String) : Header() {

    override val name = XContentSecurityPolicy.Companion.name

    companion object : HeaderCompanion {
        override val name = "X-Content-Security-Policy"
        override fun from(value: String) = XContentSecurityPolicy(value)
    }
}

class XSSProtection internal constructor(override val value: String) : Header() {

    override val name = XSSProtection.Companion.name

    companion object : HeaderCompanion {
        override val name = "X-XSS-Protection"
        override fun from(value: String) = XSSProtection(value)
    }
}

class XContentTypeOptions internal constructor(override val value: String) : Header() {

    override val name = XContentTypeOptions.Companion.name

    companion object : HeaderCompanion {
        override val name = "X-Content-Type-Options"
        override fun from(value: String) = XContentTypeOptions(value)
    }
}

class XPoweredBy internal constructor(override val value: String) : Header() {

    override val name = XPoweredBy.Companion.name

    companion object : HeaderCompanion {
        override val name = "X-Powered-By"
        override fun from(value: String) = XPoweredBy(value)
    }
}

class XUACompatible internal constructor(override val value: String) : Header() {

    override val name = XUACompatible.Companion.name

    companion object : HeaderCompanion {
        override val name = "X-UA-Compatible"
        override fun from(value: String) = XUACompatible(value)
    }
}

class XContentDuration internal constructor(override val value: String) : Header() {

    override val name = XContentDuration.Companion.name

    companion object : HeaderCompanion {
        override val name = "X-Content-Duration"
        override fun from(value: String) = XContentDuration(value)
    }
}

class XRequestID internal constructor(override val value: String) : Header() {

    override val name = XRequestID.Companion.name

    companion object : HeaderCompanion {
        override val name = "X-Request-ID"
        override fun from(value: String) = XRequestID(value)
    }
}

class XCorrelationID internal constructor(override val value: String) : Header() {

    override val name = XCorrelationID.Companion.name

    companion object : HeaderCompanion {
        override val name = "X-Correlation-ID"
        override fun from(value: String) = XCorrelationID(value)
    }
}
