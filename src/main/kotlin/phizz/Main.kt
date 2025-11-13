package phizz

import com.sun.jna.Native
import com.sun.jna.ptr.IntByReference
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
            logger.trace { "Entering dvdnav_open(path='$finalDvdIsoPath')" }
            val dvdNavHandle = libDvdNav.dvdnav_open(finalDvdIsoPath)
            logger.info { "dvdnav_open returned handle $dvdNavHandle" }
            logger.trace { "Exiting dvdnav_open" }

            if (dvdNavHandle != null) {
                val numTitlesRef = IntByReference()
                logger.trace { "Entering dvdnav_get_number_of_titles(dvdnav=$dvdNavHandle)" }
                val getTitlesResult = libDvdNav.dvdnav_get_number_of_titles(dvdNavHandle, numTitlesRef)
                logger.info { "dvdnav_get_number_of_titles returned $getTitlesResult, num_titles=${numTitlesRef.value}" }
                logger.trace { "Exiting dvdnav_get_number_of_titles" }

                logger.trace { "Entering dvdnav_close(dvdnav=$dvdNavHandle)" }
                libDvdNav.dvdnav_close(dvdNavHandle)
                logger.info { "Closed LibDvdNav handle" }
                logger.trace { "Exiting dvdnav_close" }
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
            logger.trace { "Entering bd_open(device='$finalBluRayIsoPath', keyfile=null)" }
            val bluRayHandle = libBluRay.bd_open(finalBluRayIsoPath, null)
            logger.info { "bd_open returned handle $bluRayHandle" }
            logger.trace { "Exiting bd_open" }

            if (bluRayHandle != null) {
                logger.trace { "Entering bd_get_titles(bd=$bluRayHandle, flags=0, min_title_length=0)" }
                val numTitles = libBluRay.bd_get_titles(bluRayHandle, 0, 0)
                logger.info { "bd_get_titles returned $numTitles titles" }
                logger.trace { "Exiting bd_get_titles" }

                if (numTitles > 0) {
                    val titleToSelect = 0 // Select the first title for testing
                    logger.trace { "Entering bd_select_title(bd=$bluRayHandle, title=$titleToSelect)" }
                    val selectResult = libBluRay.bd_select_title(bluRayHandle, titleToSelect)
                    logger.info { "bd_select_title($titleToSelect) returned $selectResult" }
                    logger.trace { "Exiting bd_select_title" }
                }

                logger.trace { "Entering bd_close(bd=$bluRayHandle)" }
                libBluRay.bd_close(bluRayHandle)
                logger.info { "Closed LibBluRay handle" }
                logger.trace { "Exiting bd_close" }
            }
        }
    } else {
        logger.info { "No media selected. Please specify --dvd or --bluray with a path to an ISO file." }
    }

    logger.info { "Application finished" }
}
