package com.moonhappy.phizz.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

class LibraryServiceTest {

    @Test
    fun `scanLibrary finds iso files in directory`(@TempDir tempDir: Path) {
        // Arrange
        val iso1 = File(tempDir.toFile(), "movie1.iso")
        iso1.createNewFile()
        val iso2 = File(tempDir.toFile(), "movie2.ISO") // Case insensitive check
        iso2.createNewFile()
        val txtFile = File(tempDir.toFile(), "readme.txt")
        txtFile.createNewFile()
        val subDir = File(tempDir.toFile(), "subdir")
        subDir.mkdir()
        val iso3 = File(subDir, "movie3.iso")
        iso3.createNewFile()

        val service = LibraryService()

        // Act
        val result = service.scanLibrary(tempDir.toString())

        // Assert
        assertEquals(3, result.size)
        assertTrue(result.any { it.filename == "movie1.iso" })
        assertTrue(result.any { it.filename == "movie2.ISO" })
        assertTrue(result.any { it.filename == "movie3.iso" })
    }

    @Test
    fun `scanLibrary returns empty list for non-existent directory`() {
        val service = LibraryService()
        val result = service.scanLibrary("/path/to/nowhere")
        assertTrue(result.isEmpty())
    }
}
