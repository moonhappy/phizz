package phizz

import com.sun.jna.Native

fun main() {
    val lib = Native.load("dvdnav", LibDvdNav::class.java)
    val handle = lib.dvdnav_open("/non/existent/file.iso")
    println("Handle: $handle")
    if (handle != null) {
        lib.dvdnav_close(handle)
    }
    println("Closed handle")
}
