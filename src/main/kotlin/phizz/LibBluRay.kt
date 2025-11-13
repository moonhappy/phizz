package phizz

import com.sun.jna.Library
import com.sun.jna.Pointer

interface LibBluRay : Library {
    fun bd_open(device: String, keyfile: String?): Pointer?
    fun bd_close(bd: Pointer?)
    fun bd_get_titles(bd: Pointer?, flags: Byte, min_title_length: Int): Int
    fun bd_select_title(bd: Pointer?, title: Int): Int
}
