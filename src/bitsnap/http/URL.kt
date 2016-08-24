package bitsnap.http

import bitsnap.exceptions.InvalidURLException
import java.net.MalformedURLException
import java.net.URL as JavaURL

data class URL internal constructor(internal val javaURL: JavaURL, val isRelative: Boolean) {

    override fun equals(other: Any?) = if (other is URL) {
        javaURL == other.javaURL && isRelative == other.isRelative
    } else false

    override fun toString() = if (isRelative) {
        javaURL.toString().removePrefix("file:")
    } else {
        javaURL.toString()
    }

    val protocol: String = javaURL.protocol ?: ""
    val host: String = javaURL.host ?: ""
    val port = javaURL.port
    val file: String = javaURL.file ?: ""
    val query: String = javaURL.query ?: ""
    val authority: String = javaURL.authority ?: ""
    val path: String = javaURL.path ?: ""
    val userInfo: String = javaURL.userInfo ?: ""
    val ref: String = javaURL.ref ?: ""

    companion object {

        operator fun invoke(value: String) = if (value.startsWith("/")) {
            try {
                URL(JavaURL("file", "", value), true)
            } catch (e: MalformedURLException) {
                throw InvalidURLException("$value ${e.message}")
            }
        } else {
            try {
                URL(JavaURL(value), false)
            } catch(e: MalformedURLException) {
                throw InvalidURLException("$value ${e.message}")
            }
        }
    }
}
