package bitsnap.reflect

import kotlin.reflect.KClass

/**
 *
 */
data class ClassResource internal constructor(val name: String) {

    val javaClass : Class<*> by lazy {
        Class.forName(name)
    }

    val kotlinClass : KClass<*> by lazy {
        javaClass.kotlin
    }

    val className : String by lazy {
        name.substringAfterLast(".")
    }

    val packageName : String by lazy {
        name.substringBeforeLast(".")
    }
}
