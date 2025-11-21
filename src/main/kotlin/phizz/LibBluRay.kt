package phizz

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.Structure

@Structure.FieldOrder(
    "pkt_count", "still_mode", "still_time",
    "video_stream_count", "audio_stream_count", "pg_stream_count", "ig_stream_count",
    "sec_audio_stream_count", "sec_video_stream_count"
)
open class BluRayClipInfo : Structure {
    @JvmField var pkt_count: Int = 0
    @JvmField var still_mode: Byte = 0
    @JvmField var still_time: Short = 0
    @JvmField var video_stream_count: Byte = 0
    @JvmField var audio_stream_count: Byte = 0
    @JvmField var pg_stream_count: Byte = 0
    @JvmField var ig_stream_count: Byte = 0
    @JvmField var sec_audio_stream_count: Byte = 0
    @JvmField var sec_video_stream_count: Byte = 0

    constructor() : super()
    constructor(p: Pointer?) : super(p) {
        read()
    }
}

@Structure.FieldOrder(
    "idx", "playlist", "duration", "clip_count", "angle_count",
    "chapter_count", "mark_count", "clips", "chapters", "marks", "mvc_base_view_r_flag"
)
open class BluRayTitleInfo : Structure {
    @JvmField var idx: Int = 0
    @JvmField var playlist: Int = 0
    @JvmField var duration: Long = 0
    @JvmField var clip_count: Int = 0
    @JvmField var angle_count: Byte = 0
    @JvmField var chapter_count: Int = 0
    @JvmField var mark_count: Int = 0
    @JvmField var clips: Pointer? = null
    @JvmField var chapters: Pointer? = null
    @JvmField var marks: Pointer? = null
    @JvmField var mvc_base_view_r_flag: Byte = 0

    constructor() : super()
    constructor(p: Pointer?) : super(p) {
        read()
    }
}


interface LibBluRay : Library {
    fun bd_open(path: String, unused: String?): Pointer?
    fun bd_close(handle: Pointer?)
    fun bd_get_titles(handle: Pointer?, flags: Int, min_title_length: Int): Int
    fun bd_select__title(handle: Pointer?, title: Int): Int
    fun bd_get_title_info(handle: Pointer?, title_index: Int, angle_index: Int): Pointer?

    companion object {
        const val TITLES_ALL = 0
        const val TITLES_RELEVANT = 1

        val INSTANCE: LibBluRay by lazy {
            Native.load("bluray", LibBluRay::class.java)
        }
    }
}
