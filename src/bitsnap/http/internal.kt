package bitsnap.http

internal fun String.splitParameters(withException: (String) -> Throwable) = this.split(";")
    .map { it.trim() }
    .filter { it.isNotEmpty() && it.startsWith("q=") }
    .map({
        if (it.indexOf("=") > 0) {
            val (name, param) = it.split("=")
            Pair(name, param)
        } else {
            throw withException(it)
        }
    }).associate { it }

internal fun String.splitParameters() = this.splitParameters() { HttpParseException(it) }
