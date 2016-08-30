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

package bitsnap

import bitsnap.exceptions.ActionNotFoundException
import bitsnap.http.Method
import java.util.*

class Router {

    class Builder(val routeBuilders: MutableMap<String, Route.Builder> = HashMap()) {

        fun route(method: Method, path: String, action: Application.Request.() -> Unit) {
            val builder = routeBuilders[path] ?: Route.Builder()
            builder.action(method, action)
        }

        internal var every : Application.Request.() -> Unit = {}

        fun every(action: Application.Request.() -> Unit) {
            if (every == {}) {
                this.every = action
            }
        }

        internal val everyMethod = Route.Builder()
        fun every(method: Method, action: Application.Request.() -> Unit) = everyMethod.action(method, action)

        internal operator fun invoke() = routeBuilders.entries.asSequence()
            .map { Pair(it.key, it.value.invoke()) }
            .associate { it } + Pair("_\\o/_", everyMethod)
    }
}

class Route(private val actions: Map<Method, Application.Request.() -> Unit>) {

    fun run(method: Method, appRequest: Application.Request) = runOrElse(method, appRequest) {
        throw ActionNotFoundException(method, appRequest.request.url.toString())
    }

    fun <T> runOrElse (method: Method, appRequest: Application.Request, doThis: () -> T) : T? {
        val action = actions[method]
        if (action == null) {
            return doThis()
        } else {
            appRequest.action()
            return null
        }
    }

    class Builder(private val actions: MutableMap<Method, Application.Request.() -> Unit> = EnumMap(Method::class.java)) {
        internal fun action(method: Method, action: Application.Request.() -> Unit) : Unit = if (actions.put(method, action) == null) {

        } else Unit

        internal operator fun invoke() = Route(actions)
    }
}
