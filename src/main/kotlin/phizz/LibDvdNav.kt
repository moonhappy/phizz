package phizz

import com.sun.jna.Library
import com.sun.jna.Pointer

interface LibDvdNav : Library {
    fun dvdnav_open(path: String): Pointer?
    fun dvdnav_close(dvdnav: Pointer?)
}
