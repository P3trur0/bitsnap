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

class BodyParseException(message: String) : HttpException(message)

class UnknownCharsetException(name: String) : HttpException("Unknown charset $name")

class UnknownStatusException(value: String) : HttpException("Invalid Http Status $value")

class InvalidStatusException(value: String) : HttpException("Invalid Http Status $value")

class InvalidURLException(value: String) : HeaderException("Invalid URL $value")

class InvalidRangeValue(value: String) : HeaderException("Invalid Content-Range value $value")
