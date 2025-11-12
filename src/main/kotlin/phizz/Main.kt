package phizz

import com.sun.jna.Native
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main() {
    logger.info { "Application starting" }

    // Test LibDvdNav
    val libDvdNav = Native.load("dvdnav", LibDvdNav::class.java)
    val dvdNavHandle = libDvdNav.dvdnav_open("/non/existent/dvd.iso")
    logger.info { "LibDvdNav Handle: $dvdNavHandle" }
    if (dvdNavHandle != null) {
        libDvdNav.dvdnav_close(dvdNavHandle)
        logger.info { "Closed LibDvdNav handle" }
    }

    // Test LibBluRay
    val libBluRay = Native.load("bluray", LibBluRay::class.java)
    val bluRayHandle = libBluRay.bd_open("/non/existent/bluray.iso", null)
    logger.info { "LibBluRay Handle: $bluRayHandle" }
    if (bluRayHandle != null) {
        libBluRay.bd_close(bluRayHandle)
        logger.info { "Closed LibBluRay handle" }
    }

    logger.info { "Application finished" }
}
