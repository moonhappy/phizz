package phizz

import com.sun.jna.Library
import com.sun.jna.Pointer

interface LibBluRay : Library {
    fun bd_open(device: String, keyfile: String?): Pointer?
    fun bd_close(bd: Pointer?)
}
