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

open class HeaderException(message: String) : HttpException(message)

class InvalidHeaderException(header: String) : HeaderException("Invalid header $header")

class HeaderDateParseException(value: String) : HeaderException("Can't parse Date $value")

class InvalidMimeTypeException(message: String) : HttpException("MimeType $message is invalid")

open class RequestBuilderException(massage: String) : HttpException(massage)

class RequestHeaderAlreadyAssignedException(name: String) : RequestBuilderException("Request header $name have been assigned already")

class RequestBodyAlreadyAssignedException : RequestBuilderException("Request body have been assigned already")

class UnknownSecurityDirectiveException(value: String) : HeaderException("Unknown Security Directive $value")

class UnknownStatusException(value: String) : HttpException("Invalid Http Status $value")

class InvalidStatusException(value: String) : HttpException("Invalid Http Status $value")

class InvalidURLException(value: String) : HeaderException("Invalid URL $value")
