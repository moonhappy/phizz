package phizz

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference

interface LibDvdNav : Library {
    fun dvdnav_open(dest: PointerByReference, path: String): Int
    fun dvdnav_close(dvdnav: Pointer?)
    fun dvdnav_get_number_of_titles(dvdnav: Pointer?, num_titles: IntByReference): Int
    fun dvdnav_get_next_block(dvdnav: Pointer?, buf: Pointer?, event: IntByReference, len: IntByReference): Int

    companion object {
        const val DVDNAV_BLOCK_OK = 0
        const val DVDNAV_NOP = 1
        const val DVDNAV_STILL_FRAME = 2
        const val DVDNAV_SPU_STREAM_CHANGE = 3
        const val DVDNAV_AUDIO_STREAM_CHANGE = 4
        const val DVDNAV_VTS_CHANGE = 5
        const val DVDNAV_CELL_CHANGE = 6
        const val DVDNAV_NAV_PACKET = 7
        const val DVDNAV_STOP = 8
        const val DVDNAV_HIGHLIGHT = 9
        const val DVDNAV_HOP_CHANNEL = 10
        const val DVDNAV_WAIT = 11

        val INSTANCE: LibDvdNav by lazy {
            Native.load("dvdnav", LibDvdNav::class.java)
        }
    }
}
