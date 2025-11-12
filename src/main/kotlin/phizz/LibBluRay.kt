package phizz

import com.sun.jna.Library
import com.sun.jna.Pointer
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

interface LibBluRay : Library {
    fun bd_open(device: String, keyfile: String?): Pointer? {
        logger.trace { "Entering bd_open(device='$device', keyfile=$keyfile)" }
        val result = bd_open(device, keyfile)
        logger.info { "bd_open returned handle $result" }
        logger.trace { "Exiting bd_open" }
        return result
    }

    fun bd_close(bd: Pointer?) {
        logger.trace { "Entering bd_close(bd=$bd)" }
        bd_close(bd)
        logger.trace { "Exiting bd_close" }
    }

    fun bd_get_titles(bd: Pointer?, flags: Byte, min_title_length: Int): Int {
        logger.trace { "Entering bd_get_titles(bd=$bd, flags=$flags, min_title_length=$min_title_length)" }
        val result = bd_get_titles(bd, flags, min_title_length)
        logger.info { "bd_get_titles returned $result titles" }
        logger.trace { "Exiting bd_get_titles" }
        return result
    }

    fun bd_select_title(bd: Pointer?, title: Int): Int {
        logger.trace { "Entering bd_select_title(bd=$bd, title=$title)" }
        val result = bd_select_title(bd, title)
        logger.info { "bd_select_title($title) returned $result" }
        logger.trace { "Exiting bd_select_title" }
        return result
    }
}
