package phizz

import com.moonhappy.phizz.model.IsoFile
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.application.*

class ServerTest {

    @Test
    fun testHealthEndpoint() = testApplication {
        application {
            module()
        }

        val response = client.get("/health")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Healthy", response.bodyAsText())
    }

    @Test
    fun testLibraryEndpoint(@TempDir tempDir: Path) = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            module(tempDir.toString())
        }
        val iso1 = File(tempDir.toFile(), "movie1.iso")
        iso1.createNewFile()
        val iso2 = File(tempDir.toFile(), "movie2.ISO")
        iso2.createNewFile()

        val response = client.get("/library")

        assertEquals(HttpStatusCode.OK, response.status)

        val isoFiles = Json.decodeFromString<List<IsoFile>>(response.bodyAsText())
        assertEquals(2, isoFiles.size)
        assertTrue(isoFiles.any { it.filename == "movie1.iso" })
        assertTrue(isoFiles.any { it.filename == "movie2.ISO" })
    }
}
