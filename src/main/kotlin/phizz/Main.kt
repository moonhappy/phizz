package phizz

import com.sun.jna.Native
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    logger.info { "Application starting" }

    var dvdIsoPath: String? = null
    var bluRayIsoPath: String? = null

    args.forEachIndexed { index, arg ->
        when (arg) {
            "--dvd" -> {
                if (index + 1 < args.size) {
                    dvdIsoPath = args[index + 1]
                }
            }
            "--bluray" -> {
                if (index + 1 < args.size) {
                    bluRayIsoPath = args[index + 1]
                }
            }
        }
    }

    val finalDvdIsoPath = dvdIsoPath ?: "/non/existent/dvd.iso"
    val finalBluRayIsoPath = bluRayIsoPath ?: "/non/existent/bluray.iso"

    // Test LibDvdNav
    val libDvdNav = Native.load("dvdnav", LibDvdNav::class.java)
    val dvdNavHandle = libDvdNav.dvdnav_open(finalDvdIsoPath)
    logger.info { "LibDvdNav Handle: $dvdNavHandle" }
    if (dvdNavHandle != null) {
        libDvdNav.dvdnav_close(dvdNavHandle)
        logger.info { "Closed LibDvdNav handle" }
    }

    // Test LibBluRay
    val libBluRay = Native.load("bluray", LibBluRay::class.java)
    val bluRayHandle = libBluRay.bd_open(finalBluRayIsoPath, null)
    logger.info { "LibBluRay Handle: $bluRayHandle" }
    if (bluRayHandle != null) {
        libBluRay.bd_close(bluRayHandle)
        logger.info { "Closed LibBluRay handle" }
    }

    logger.info { "Application finished" }
}
