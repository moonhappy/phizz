package com.moonhappy.phizz.service

import com.moonhappy.phizz.model.IsoFile
import java.io.File

class LibraryService {

    fun scanLibrary(directoryPath: String): List<IsoFile> {
        val isoFiles = mutableListOf<IsoFile>()
        val rootDirectory = File(directoryPath)

        if (!rootDirectory.exists() || !rootDirectory.isDirectory) {
            println("Error: Directory not found or is not a directory: $directoryPath")
            return emptyList()
        }

        rootDirectory.walkTopDown().forEach { file ->
            if (file.isFile && file.extension.equals("iso", ignoreCase = true)) {
                isoFiles.add(IsoFile(file.name, file.absolutePath, file.length()))
            }
        }
        return isoFiles
    }
}
