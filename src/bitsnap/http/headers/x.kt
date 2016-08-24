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

        init {
            Header.registerCompanion(XFrameOptions.Companion)
        }

        override val name = "X-Frame-Options"
        override operator fun invoke(value: String) = XFrameOptions(value)
    }
}


class XSSProtection internal constructor(override val value: String) : Header() {

    override val name = XSSProtection.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(XSSProtection.Companion)
        }

        override val name = "X-XSS-Protection"
        override operator fun invoke(value: String) = XSSProtection(value)
    }
}

class XContentTypeOptions internal constructor(override val value: String) : Header() {

    override val name = XContentTypeOptions.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(XContentTypeOptions.Companion)
        }

        override val name = "X-Content-Type-Options"
        override operator fun invoke(value: String) = XContentTypeOptions(value)
    }
}

class XPoweredBy internal constructor(override val value: String) : Header() {

    override val name = XPoweredBy.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(XPoweredBy.Companion)
        }

        override val name = "X-Powered-By"
        override operator fun invoke(value: String) = XPoweredBy(value)
    }
}

class XUACompatible internal constructor(override val value: String) : Header() {

    override val name = XUACompatible.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(XUACompatible.Companion)
        }

        override val name = "X-UA-Compatible"
        override operator fun invoke(value: String) = XUACompatible(value)
    }
}

class XContentDuration internal constructor(override val value: String) : Header() {

    override val name = XContentDuration.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(XContentDuration.Companion)
        }

        override val name = "X-Content-Duration"
        override operator fun invoke(value: String) = XContentDuration(value)
    }
}

class XRequestID internal constructor(override val value: String) : Header() {

    override val name = XRequestID.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(XRequestID.Companion)
        }

        override val name = "X-Request-ID"
        override operator fun invoke(value: String) = XRequestID(value)
    }
}

class XCorrelationID internal constructor(override val value: String) : Header() {

    override val name = XCorrelationID.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(XCorrelationID.Companion)
        }

        override val name = "X-Correlation-ID"
        override operator fun invoke(value: String) = XCorrelationID(value)
    }
}
