package phizz

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.Structure

@Structure.FieldOrder(
    "duration", "chapter_count", "playlist", "clip_count", "angle_count",
    "video_stream_count", "audio_stream_count", "pg_stream_count", "ig_stream_count"
)
open class BluRayTitleInfo : Structure {
    @JvmField var duration: Int = 0
    @JvmField var chapter_count: Int = 0
    @JvmField var playlist: Int = 0
    @JvmField var clip_count: Int = 0
    @JvmField var angle_count: Int = 0
    @JvmField var video_stream_count: Int = 0
    @JvmField var audio_stream_count: Int = 0
    @JvmField var pg_stream_count: Int = 0
    @JvmField var ig_stream_count: Int = 0

    constructor() : super()
    constructor(p: Pointer?) : super(p) {
        read()
    }
}


interface LibBluRay : Library {
    fun bd_open(path: String, unused: String?): Pointer?
    fun bd_close(handle: Pointer?)
    fun bd_get_titles(handle: Pointer?, flags: Int): Int
    fun bd_select_title(handle: Pointer?, title: Int): Int
    fun bd_get_title_info(handle: Pointer?, title_index: Int, angle_index: Int): Pointer?

    companion object {
        val INSTANCE: LibBluRay by lazy {
            Native.load("bluray", LibBluRay::class.java)
        }
    }
}
