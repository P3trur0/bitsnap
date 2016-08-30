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

class ContentSecurityPolicy internal constructor(override val value: String) : Header() {

    override val name = ContentSecurityPolicy.Companion.name

    abstract class DirectiveType(val name: String) {

        abstract val value: String

        override fun toString() = listOf(name, value)
            .filter { it.isNotBlank() }.joinToString(" ")
    }

    abstract class TargetedDirectiveType(name: String, val target: Target, val source: Source) : DirectiveType(name) {
        override val value by lazy {
            listOf(target.toString(), source.toString())
                .filter { it.isNotBlank() }.joinToString(" ")
        }
    }

    enum class Target(val targetName: String) {
        Empty(""),
        None("'none'"),
        Self("'self'"),
        UnsafeInline("'unsafe-inline'"),
        UnsafeEval("'unsafe-eval'");

        override fun toString() = targetName
    }

    sealed class Source(val content: String) {
        class Specific(content: String) : Source(content)
        object Empty : Source("")
        object Https : Source("https:")
        class URL(url: bitsnap.http.URL) : Source(url.toString())
        class Data : Source("data:")
        class MediaStream : Source("mediastream:")
        class Blob : Source("blob:")
        class Filesystem : Source("filesystem:")

        override fun toString() = content
    }

    object Directive {
        class BaseURI(target: Target, source: Source) : TargetedDirectiveType("base-uri", target, source)
        class ChildSource(target: Target, source: Source) : TargetedDirectiveType("child-uri", target, source)
        class ConnectSource(target: Target, source: Source) : TargetedDirectiveType("connect-src", target, source)
        class DefaultSource(target: Target, source: Source) : TargetedDirectiveType("default-src", target, source)
        class FontSource(target: Target, source: Source) : TargetedDirectiveType("font-src", target, source)
        class FormAction(target: Target, source: Source) : TargetedDirectiveType("form-action", target, source)
        class FrameAncestors(target: Target, source: Source) : TargetedDirectiveType("frame-ancestors", target, source)
        class FrameSource(target: Target, source: Source) : TargetedDirectiveType("frame-src", target, source)
        class ImageSource(target: Target, source: Source) : TargetedDirectiveType("img-src", target, source)
        class ManifestSource(target: Target, source: Source) : TargetedDirectiveType("manifest-src", target, source)
        class MediaSource(target: Target, source: Source) : TargetedDirectiveType("media-src", target, source)
        class ObjectSource(target: Target, source: Source) : TargetedDirectiveType("object-src", target, source)
        class PluginTypes(target: Target, source: Source) : TargetedDirectiveType("plugin-types", target, source)

        class Referrer(val target: Referrer.Target) : DirectiveType("referrer") {
            enum class Target(val targetName: String) {
                NoReferrer("no-referrer"),
                NoReferrerWhenDowngrade("no-referrer-when-downgrade"),
                Origin("origin"),
                OriginWhenCrossOrigin("origin-when-cross-origin"),
                UnsafeUrl("unsafe-url");

                override fun toString() = targetName
            }

            override val value = target.toString()
        }

        class ReflectedXSS(target: ReflectedXSS.Target) : DirectiveType("reflected-xss") {
            enum class Target {
                Allow,
                Block,
                Filter;

                override fun toString() = this.name.toLowerCase()
            }

            override val value = target.toString()
        }

        class ReportURI(target: Target, url: Source.URL) : TargetedDirectiveType("report-uri", target, url)
        class Sandbox(target: Target, source: Source.Specific) : TargetedDirectiveType("sandbox", target, source)
        class ScriptSource(target: Target, source: Source.Specific) : TargetedDirectiveType("script-src", target, source)
        class StrictDynamic(override val value: String) : DirectiveType("script-src 'strict-dynamic'")
        class StyleSource(target: Target, source: Source) : TargetedDirectiveType("style-src", target, source)
        class UpgradeInsecureRequests : DirectiveType("upgrade-insecure-requests") {
            override val value = ""
            override fun toString() = name
        }
    }

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(ContentSecurityPolicy.Companion)
        }

        override val name = "Content-Security-Policy"
        override operator fun invoke(value: String) = ContentSecurityPolicy(value)
    }
}

class XContentSecurityPolicy internal constructor(override val value: String) : Header() {

    override val name = XContentSecurityPolicy.Companion.name

    companion object : HeaderCompanion {

        init {
            Header.registerCompanion(XContentSecurityPolicy.Companion)
        }

        override val name = "X-Content-Security-Policy"
        override operator fun invoke(value: String) = XContentSecurityPolicy(value)
    }
}
