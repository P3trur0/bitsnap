package bitsnap.http.headers

import bitsnap.http.Header

internal fun <T> joinToStringWithQuality(values: List<Pair<T, Int>>) =
    values.joinToString {
        "${it.toString()}${
        if (it.second < 10 && it.second >= 0)
            ";q=0.${it.second}"
        else ""
        },"
    }.removeSuffix(",")

@Throws(Header.HeaderParseException::class)
internal fun String.splitValueQuality() = this.split(",")
    .map{ it.trim() }
    .map {
        val qualityIndex = it.indexOfAny(listOf(";q=", "; q="))
        return@map if (qualityIndex > 0) {
            val value = (it.substring(0, qualityIndex))
            var qualityString = it.substring(qualityIndex)
            // there might be some other parameters
            val qualitySemIndex = qualityString.indexOf(';')
            if (qualitySemIndex > 0) {
                qualityString = qualityString.substring(0, qualitySemIndex)
            }

            try {
                val quality = (qualityString.toFloat() * 10).toInt()
                Pair(value, quality)
            } catch(e: NumberFormatException) {
                throw Header.HeaderParseException("Cant parse header quality $qualityString")
            }
        } else {
            Pair(it, 10)
        }
    }
