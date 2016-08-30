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

open class HeaderException(message: String) : HttpException(message)

class AlreadyAssignedException(name: String) : HeaderException("$name have been assigned already")

class InvalidHeaderException(header: String) : HeaderException("Invalid header $header")

class InvalidMimeTypeException(message: String) : HeaderException("MimeType $message")

class InvalidCookie(value: String) : HeaderException("Invalid cookie $value")

class InvalidHeaderQualityException(quality: String) : HeaderException("Invalid header quality $quality")

class HeaderDateParseException(value: String) : HeaderException("Can't parse Date $value")

class UnknownEncodingException(name: String) : HeaderException("Unknown $name encoding")

class InvalidCacheControlException(value: String) : HeaderException("Invalid Cache-Control $value")

class UnknownCacheControlException(value: String) : HeaderException("Unknown Cache-Control $value")

class UnknownVaryHeader(name: String) : HeaderException("Unknown Vary Header $name")

class InvalidContentDispositionException(parameter: String) : HeaderException("Invalid Content-Disposition parameter $parameter")

class InvalidContentLengthException(length: String) : HeaderException("Invalid Content-Length $length")

class UnknownTrackingStatus(value: String) : HeaderException("Unknown tracking status $value")

class HeaderDuplicateException(name: String, value: String) : HeaderException("$name duplicate $value")

class InvalidQualityValueException(name: String, quality: String) : HeaderException("Invalid $name quality $quality")

class AllowMethodParseException(method: String) : HeaderException("Unknown HTTP method $method")

class InvalidMD5Value(value: String) : HeaderException("Invalid MD5 $value")

class UnknownSecurityDirectiveException(value: String) : HeaderException("Unknown Security Directive $value")
