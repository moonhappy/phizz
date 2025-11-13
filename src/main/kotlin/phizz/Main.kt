package phizz

import com.sun.jna.Native
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    logger.info { "Application starting" }

    var dvdIsoPath: String? = null
    var bluRayIsoPath: String? = null

    var i = 0
    while (i < args.size) {
        when (args[i]) {
            "--dvd" -> {
                if (i + 1 < args.size) {
                    dvdIsoPath = args[i + 1]
                    i++ // Consume next argument
                }
            }
            "--bluray" -> {
                if (i + 1 < args.size) {
                    bluRayIsoPath = args[i + 1]
                    i++ // Consume next argument
                }
            }
        }
        i++
    }

    if (dvdIsoPath != null) {
        val finalDvdIsoPath = java.io.File(dvdIsoPath).canonicalPath
        logger.info { "Attempting to open DVD at: $finalDvdIsoPath" }

        val dvdFile = java.io.File(finalDvdIsoPath)
        if (!dvdFile.exists() || !dvdFile.canRead()) {
            logger.error { "DVD file does not exist or cannot be read at: $finalDvdIsoPath" }
        } else {
            // Test LibDvdNav
            val libDvdNav = Native.load("dvdnav", LibDvdNav::class.java)
            val dvdNavHandle = libDvdNav.dvdnav_open(finalDvdIsoPath)
            logger.info { "LibDvdNav Handle: $dvdNavHandle" }
            if (dvdNavHandle != null) {
                libDvdNav.dvdnav_close(dvdNavHandle)
                logger.info { "Closed LibDvdNav handle" }
            }
        }
    } else if (bluRayIsoPath != null) {
        val finalBluRayIsoPath = java.io.File(bluRayIsoPath).canonicalPath
        logger.info { "Attempting to open Blu-ray at: $finalBluRayIsoPath" }

        val bluRayFile = java.io.File(finalBluRayIsoPath)
        if (!bluRayFile.exists() || !bluRayFile.canRead()) {
            logger.error { "Blu-ray file does not exist or cannot be read at: $finalBluRayIsoPath" }
        } else {
            // Test LibBluRay
            val libBluRay = Native.load("bluray", LibBluRay::class.java)
            val bluRayHandle = libBluRay.bd_open(finalBluRayIsoPath, null)
            logger.info { "LibBluRay Handle: $bluRayHandle" }
            if (bluRayHandle != null) {
                libBluRay.bd_close(bluRayHandle)
                logger.info { "Closed LibBluRay handle" }
            }
        }
    } else {
        logger.info { "No media selected. Please specify --dvd or --bluray with a path to an ISO file." }
    }

    logger.info { "Application finished" }
}
