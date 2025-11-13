package phizz

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertTrue

class MainTest {
    @Test
    fun `trivial test`() {
        assertTrue(true)
    }

    @Test
    fun `should not throw exception when called with bluray argument`() {
        val tempFile = File.createTempFile("bluray", ".iso")
        tempFile.deleteOnExit()
        main(arrayOf("--bluray", tempFile.absolutePath))
    }
}
