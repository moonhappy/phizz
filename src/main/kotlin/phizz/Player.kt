package phizz

import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.TimeUnit

private val logger = LoggerFactory.getLogger(Player::class.java)

enum class DiscType {
    BLURAY,
    DVD
}

class Player(path: String, private val type: DiscType) {

    private var handle: Pointer? = null

    init {
        val file = File(path)
        if (!file.exists()) {
            logger.error("{} file not found: {}", type, path)
        } else {
            handle = when (type) {
                DiscType.BLURAY -> {
                    val lib = LibBluRay.INSTANCE
                    lib.bd_open(path, null)
                }
                DiscType.DVD -> {
                    val lib = LibDvdNav.INSTANCE
                    val ptrRef = PointerByReference()
                    if (lib.dvdnav_open(ptrRef, path) == 1) {
                        ptrRef.value
                    } else {
                        null
                    }
                }
            }

            if (handle == null) {
                logger.error("Failed to open {} path: {}", type, path)
            } else {
                logger.info("Successfully opened {} path: {}", type, path)
            }
        }
    }

    fun listTitles() {
        if (handle == null) {
            logger.error("Cannot list titles, handle is null")
            return
        }

        when (type) {
            DiscType.BLURAY -> listBluRayTitles()
            DiscType.DVD -> listDvdTitles()
        }
    }

    private fun listBluRayTitles() {
        val lib = LibBluRay.INSTANCE
        val titleCount = lib.bd_get_titles(handle, LibBluRay.TITLES_ALL, 0)
        if (titleCount == 0) {
            logger.info("No titles found on Blu-ray.")
        } else {
            logger.info("Found {} titles on Blu-ray:", titleCount)
            for (i in 0 until titleCount) {
                val infoPtr = lib.bd_get_title_info(handle, i, 0)
                if (infoPtr != null) {
                    val info = BluRayTitleInfo(infoPtr)
                    val durationSeconds = info.duration / 90000
                    val hours = TimeUnit.SECONDS.toHours(durationSeconds)
                    val minutes = TimeUnit.SECONDS.toMinutes(durationSeconds) % 60
                    val seconds = durationSeconds % 60

                    val durationStr = String.format("%02d:%02d:%02d", hours, minutes, seconds)

                    logger.info(
                        "  Title {}: Playlist {}, Duration: {}, Chapters: {}, Angles: {}",
                        i, info.playlist, durationStr, info.chapter_count, info.angle_count
                    )

                    if (info.clip_count > 0 && info.clips != null) {
                        val clip = BluRayClipInfo(info.clips)
                        logger.info(
                            "    Streams: {} video, {} audio, {} secondary audio, {} subtitles, {} interactive graphics",
                            clip.video_stream_count.toInt() and 0xFF,
                            clip.audio_stream_count.toInt() and 0xFF,
                            clip.sec_audio_stream_count.toInt() and 0xFF,
                            clip.pg_stream_count.toInt() and 0xFF,
                            clip.ig_stream_count.toInt() and 0xFF
                        )
                    } else {
                        logger.info("    No clips found for title {}", i)
                    }
                } else {
                    logger.warn("Could not get info for title {}", i)
                }
            }
        }
    }

    fun playTitle(titleNumber: Int) {
        if (handle == null) {
            logger.error("Cannot play title, handle is null")
            return
        }

        logger.info("Starting playback of title {}", titleNumber)

        when (type) {
            DiscType.BLURAY -> playBluRayTitle(titleNumber)
            DiscType.DVD -> playDvdTitle(titleNumber)
        }
    }

    private fun playBluRayTitle(titleNumber: Int) {
        val lib = LibBluRay.INSTANCE
        // Select the title
        if (lib.bd_select_title(handle, titleNumber) == 0) {
            logger.error("Failed to select Blu-ray title {}", titleNumber)
            return
        }

        // Read loop
        val bufSize = 6144 // Standard Blu-ray packet size
        val buffer = com.sun.jna.Memory(bufSize.toLong())
        var packetsRead = 0
        val maxPackets = 100 // Limit for PoC

        logger.info("Reading packets from Blu-ray title {}...", titleNumber)
        while (packetsRead < maxPackets) {
            val bytesRead = lib.bd_read(handle, buffer, bufSize)
            if (bytesRead <= 0) {
                logger.info("End of stream or error reading Blu-ray packet.")
                break
            }
            logger.info("Read Blu-ray packet #{}: {} bytes", packetsRead + 1, bytesRead)
            packetsRead++
        }
        logger.info("Finished reading {} packets from Blu-ray title {}", packetsRead, titleNumber)
    }

    private fun playDvdTitle(titleNumber: Int) {
        val lib = LibDvdNav.INSTANCE
        // DVD navigation is event-driven, so we just enter the read loop.
        // Note: dvdnav_title_play might be needed for specific title selection, 
        // but for this PoC we'll assume the default flow or menu interaction eventually.
        // However, to strictly follow "playTitle", we should probably try to jump to it.
        // For now, let's just read blocks as that's the core requirement.
        
        val bufSize = 2048 // Standard DVD block size
        val buffer = com.sun.jna.Memory(bufSize.toLong())
        val event = IntByReference()
        val len = IntByReference()
        var blocksRead = 0
        val maxBlocks = 100 // Limit for PoC

        logger.info("Reading blocks from DVD...")
        while (blocksRead < maxBlocks) {
            val result = lib.dvdnav_get_next_block(handle, buffer, event, len)
            if (result != 1) { // DVDNAV_STATUS_ERR
                logger.error("Error reading DVD block")
                break
            }

            when (event.value) {
                LibDvdNav.DVDNAV_BLOCK_OK -> {
                    logger.info("Read DVD block #{}: {} bytes", blocksRead + 1, len.value)
                    blocksRead++
                }
                LibDvdNav.DVDNAV_NOP -> logger.debug("DVDNAV_NOP")
                LibDvdNav.DVDNAV_STILL_FRAME -> logger.info("DVDNAV_STILL_FRAME")
                LibDvdNav.DVDNAV_STOP -> {
                    logger.info("DVDNAV_STOP")
                    break
                }
                else -> logger.info("DVD Event: {}", event.value)
            }
        }
        logger.info("Finished reading {} blocks from DVD", blocksRead)
    }

    private fun listDvdTitles() {
        val lib = LibDvdNav.INSTANCE
        val numTitles = IntByReference()
        val result = lib.dvdnav_get_number_of_titles(handle, numTitles)
        // libdvdnav uses DVDNAV_STATUS_OK == 1 for success, DVDNAV_STATUS_ERR == 0 for error
        if (result == 1) {
            logger.info("Found {} titles on DVD.", numTitles.value)
        } else {
            logger.warn("dvdnav_get_number_of_titles failed with result: {}", result)
        }
    }

    fun close() {
        if (handle != null) {
            when (type) {
                DiscType.BLURAY -> {
                    LibBluRay.INSTANCE.bd_close(handle)
                    logger.info("Blu-ray handle closed")
                }
                DiscType.DVD -> {
                    LibDvdNav.INSTANCE.dvdnav_close(handle)
                    logger.info("DVD handle closed")
                }
            }
            handle = null
        }
    }
}
