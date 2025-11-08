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
}
