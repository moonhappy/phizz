package phizz

import com.moonhappy.phizz.service.LibraryService
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.path
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.event.Level

fun startServer() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(ContentNegotiation) {
            json()
        }
        install(CallLogging) {
            level = Level.INFO
            filter { call -> call.request.path().startsWith("/") }
        }
        module()
    }.start(wait = true)
}

fun Application.module() {
    val libraryService = LibraryService()
    // TODO: The library path should be configurable, not hardcoded.
    val libraryPath = "/tmp/iso_library"

    routing {
        get("/library") {
            val isoFiles = libraryService.scanLibrary(libraryPath)
            call.respond(isoFiles)
        }
        get("/health") {
            call.respondText("Healthy")
        }
    }
}
