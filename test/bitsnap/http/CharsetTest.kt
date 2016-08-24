package bitsnap.http

import org.jetbrains.spek.api.Spek
import kotlin.test.assertNotNull

class CharsetTest : Spek({
    describe("HTTP ANSI Charset") {
        it("should exist") {
            assertNotNull(Charset.ANSI.nioCharset.name())
        }
    }
})
