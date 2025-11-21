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

    companion object {
        val INSTANCE: LibDvdNav by lazy {
            Native.load("dvdnav", LibDvdNav::class.java)
        }
    }
}
