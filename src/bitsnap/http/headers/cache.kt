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

class ETag internal constructor(override val value: String) : Header() {

    override val name = ETag.Companion.name

    companion object : HeaderCompanion {
        override val name = "ETag"
        override fun from(value: String) = ETag(value)
    }
}

class Expires internal constructor(override val value: String) : Header() {

    override val name = Expires.Companion.name

    companion object : HeaderCompanion {
        override val name = "Expires"
        override fun from(value: String) = Expires(value)
    }
}

class CacheControl internal constructor(override val value: String) : Header() {

    override val name = CacheControl.Companion.name

    companion object : HeaderCompanion {
        override val name = "Cache-Control"
        override fun from(value: String) = CacheControl(value)
    }
}

class Vary internal constructor(override val value: String) : Header() {

    override val name = Vary.Companion.name

    companion object : HeaderCompanion {
        override val name = "Vary"
        override fun from(value: String) = Vary(value)
    }
}

class IfMatch internal constructor(override val value: String) : Header() {

    override val name = IfMatch.Companion.name

    companion object : HeaderCompanion {
        override val name = "If-Match"
        override fun from(value: String) = IfMatch(value)
    }
}

class IfModifiedSince internal constructor(override val value: String) : Header() {

    override val name = IfModifiedSince.Companion.name

    companion object : HeaderCompanion {
        override val name = "If-Modified-Since"
        override fun from(value: String) = IfModifiedSince(value)
    }
}

class IfNoneMatch internal constructor(override val value: String) : Header() {

    override val name = IfNoneMatch.Companion.name

    companion object : HeaderCompanion {
        override val name = "If-None-Match"
        override fun from(value: String) = IfNoneMatch(value)
    }
}

class IfRange internal constructor(override val value: String) : Header() {

    override val name = IfRange.Companion.name

    companion object : HeaderCompanion {
        override val name = "If-Range"
        override fun from(value: String) = IfRange(value)
    }
}

class IfUnmodifiedSince internal constructor(override val value: String) : Header() {

    override val name = IfUnmodifiedSince.Companion.name

    companion object : HeaderCompanion {
        override val name = "If-Unmodified-Since"
        override fun from(value: String) = IfUnmodifiedSince(value)
    }
}

