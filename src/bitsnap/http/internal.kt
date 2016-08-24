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

package bitsnap.http

import bitsnap.exceptions.InvalidHeaderException

internal fun String.splitParameters(splitter: String = ";", withException: (String) -> Throwable) = this.split(splitter).asSequence()
    .filter { it.isNotEmpty() && !it.startsWith("q=") } // ignoring quality parameters
    .map({
        if (it.indexOf("=") > 0) {
            val chunks = it.split("=")
            val name = chunks.component1().trim()
            val param = chunks.component2().trim()

            if (name.isBlank() || param.isBlank()) {
                throw withException(it)
            }

            if (!name.isValidParam()) {
                throw withException(name)
            }

            if (!param.isValidParam()) {
                throw withException(param)
            }

            Pair(name, param)
        } else {
            throw withException(it)
        }
    }).associate { it }

internal fun String.splitParameters() = this.splitParameters() { InvalidHeaderException(it) }

internal val forbiddenTypeChars: String = "()<>@,;:/[]?=\\\""

internal val printableRegex = "[A-Za-z0-9\\-+*.]+".toRegex()

internal fun String.isValidParam() = this.matches(printableRegex) && this.firstOrNull { forbiddenTypeChars.contains(it) } == null

