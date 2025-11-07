package phizz

import com.sun.jna.Library
import com.sun.jna.Pointer
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

interface LibDvdNav : Library {
    fun dvdnav_open(path: String): Pointer? {
        logger.trace { "Entering dvdnav_open(path='$path')" }
        val result = dvdnav_open(path)
        logger.info { "dvdnav_open returned handle $result" }
        logger.trace { "Exiting dvdnav_open" }
        return result
    }

    fun dvdnav_close(dvdnav: Pointer?) {
        logger.trace { "Entering dvdnav_close(dvdnav=$dvdnav)" }
        dvdnav_close(dvdnav)
        logger.trace { "Exiting dvdnav_close" }
    }
}
