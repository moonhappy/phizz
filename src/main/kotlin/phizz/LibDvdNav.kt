package phizz

import com.sun.jna.Library
import com.sun.jna.Pointer
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

interface LibDvdNav : Library {
    fun dvdnav_open(path: String): Pointer? {
        logger.trace { "Entering dvdnav_open(path='$path')" }
        val result = dvdnav_open_native(path)
        logger.info { "dvdnav_open returned handle $result" }
        logger.trace { "Exiting dvdnav_open" }
        return result
    }

    fun dvdnav_close(dvdnav: Pointer?) {
        logger.trace { "Entering dvdnav_close(dvdnav=$dvdnav)" }
        dvdnav_close_native(dvdnav)
        logger.trace { "Exiting dvdnav_close" }
    }

    fun dvdnav_get_number_of_titles(dvdnav: Pointer?, num_titles: com.sun.jna.ptr.IntByReference): Int {
        logger.trace { "Entering dvdnav_get_number_of_titles(dvdnav=$dvdnav)" }
        val result = dvdnav_get_number_of_titles_native(dvdnav, num_titles)
        logger.info { "dvdnav_get_number_of_titles returned $result, num_titles=${num_titles.value}" }
        logger.trace { "Exiting dvdnav_get_number_of_titles" }
        return result
    }

    fun dvdnav_open_native(path: String): Pointer?
    fun dvdnav_close_native(dvdnav: Pointer?)
    fun dvdnav_get_number_of_titles_native(dvdnav: Pointer?, num_titles: com.sun.jna.ptr.IntByReference): Int
}
