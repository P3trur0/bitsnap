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

package bitsnap.exceptions

import bitsnap.http.Method

open class BitsnapException(message: String) : Throwable(message)

open class FrameworkException(message: String) : BitsnapException(message)

open class HttpException(message: String) : BitsnapException(message)

class ActionNotFoundException(method: Method, path: String) : FrameworkException("Action $method $path not found")
