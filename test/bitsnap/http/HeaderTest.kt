package bitsnap.http

import bitsnap.reflect.ClassPath
import org.jetbrains.spek.api.Spek
import kotlin.reflect.companionObjectInstance
import kotlin.test.assertEquals

const val headersPackage = "bitsnap.http.headers "

class HeaderTest : Spek({

    describe("Header ClassResources in $headersPackage package") {
        val classpath = ClassPath.of(ClassLoader.getSystemClassLoader())
        val resourceCompanionPairs = classpath.packageClasses(headersPackage)
            .filter { !it.className.endsWith("Kt") }
            .map { Pair(it, it.kotlinClass.companionObjectInstance) }

        fun resourceCompanionFromPair(pair: Pair<ClassPath.ClassResource, Any?>):
            Pair<ClassPath.ClassResource, Header.HeaderCompanion> {

            val (resource, companionObject) = pair
            assert(companionObject is Header.HeaderCompanion)
            val companion = companionObject as Header.HeaderCompanion
            return Pair(resource, companion)
        }

        it("every companion object should implement Header.Companion.Parsable interface and it's name should match classname") {
            resourceCompanionPairs.forEach {
                val (resource, companion ) = resourceCompanionFromPair(it)
                if (!listOf( // list of classnames that don't match with header's name
                    "DoNotTrack",
                    "TrackingStatus",
                    "TransferEncodings",
                    "XSSProtection"
                ).contains(resource.className)) {
                    assertEquals(companion.name.toLowerCase().replace("-", ""), resource.className.toLowerCase())
                }
            }
        }

        it("every companion object should be registered in Headers.Companion object") {
            resourceCompanionPairs.forEach {
                val companion = resourceCompanionFromPair(it).component2()
                assert(Header.headerCompanions.containsKey(companion.name))
            }

            assertEquals(Header.headerCompanions.size, resourceCompanionPairs.size)
        }
    }
})
