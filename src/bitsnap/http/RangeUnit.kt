package bitsnap.http

sealed class RangeUnit(val name: String) {

    object Bytes : RangeUnit("bytes")

    object None : RangeUnit("none")

    class Specific(name: String) : RangeUnit(name)

    override fun equals(other: Any?) = if (other is RangeUnit) {
        name == other.name
    } else false

    companion object {
        operator fun invoke(value: String) = when (value.split(' ')[0]) {
            "bytes" -> RangeUnit.Bytes
            "none" -> RangeUnit.None
            else -> Specific(value.filter { !it.isWhitespace() })
        }
    }

    override fun toString() = name
}
