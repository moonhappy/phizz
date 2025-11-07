package phizz

import com.sun.jna.Native
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main() {
    logger.info { "Application starting" }
    val lib = Native.load("dvdnav", LibDvdNav::class.java)
    val handle = lib.dvdnav_open("/non/existent/file.iso")
    logger.info { "Handle: $handle" }
    if (handle != null) {
        lib.dvdnav_close(handle)
        logger.info { "Closed handle" }
    }
    logger.info { "Application finished" }
}
