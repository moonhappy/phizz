package phizz

import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
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
                    lib.dvdnav_open(path)
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

    private fun listDvdTitles() {
        val lib = LibDvdNav.INSTANCE
        val numTitles = IntByReference()
        val result = lib.dvdnav_get_number_of_titles(handle, numTitles)
        if (result == 0) {
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
