package phizz

import com.sun.jna.Library
import com.sun.jna.Pointer

interface LibDvdNav : Library {
    fun dvdnav_open(path: String): Pointer?
    fun dvdnav_close(dvdnav: Pointer?)
    fun dvdnav_get_number_of_titles(dvdnav: Pointer?, num_titles: com.sun.jna.ptr.IntByReference): Int
}
