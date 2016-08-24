package bitsnap.http

import bitsnap.exceptions.UnknownEncodingException

enum class Encoding(val type: String) {
    // Any encoding
    ANY("*"),
    // Deprecated compress
    COMPRESS("compress"),
    // Old Plain Deflate
    DEFLATE("deflate"),
    // W3C Efficient XML Interchange
    EXI("exi"),
    GZIP("gzip"),
    // No transformation
    IDENTITY("identity"),
    // Network Transfer Format for Java Archives
    PACK200_GZIP("pack200-gzip"),
    // Brotli
    BR("br"),
    BZIP2("bzip2"),
    LZMA("lzma"),
    PEERDIST("peerdist"),
    SDCH("sdch"),
    XPRESS("xpress"),
    XZ("xz");

    override fun toString() = type

    companion object {
        @Throws(UnknownEncodingException::class)
        operator fun invoke(value: String) = Encoding.values().find { it.type == value }
            ?: throw UnknownEncodingException("Unknown $value encoding")
    }
}
