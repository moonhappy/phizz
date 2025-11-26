package phizz

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("phizz.Main")

fun main(args: Array<String>) {
    logger.info("phizz started")

    if (args.contains("--server")) {
        logger.info("Starting server mode")
        startServer()
        logger.info("phizz finished")
        return
    }

    if (args.contains("--help") || args.contains("-h")) {
        printUsage()
        return
    }

    if (args.isEmpty()) {
        printUsage()
        return
    }

    var blurayPath: String? = null
    var dvdPath: String? = null

    var i = 0
    while (i < args.size) {
        when (args[i]) {
            "--bluray" -> {
                if (i + 1 < args.size) {
                    blurayPath = args[i + 1]
                    i++
                } else {
                    logger.error("--bluray option requires a path")
                    return
                }
            }
            "--dvd" -> {
                if (i + 1 < args.size) {
                    dvdPath = args[i + 1]
                    i++
                } else {
                    logger.error("--dvd option requires a path")
                    return
                }
            }
        }
        i++
    }

    if (blurayPath == null && dvdPath == null) {
        logger.error("No media path provided. Use --bluray or --dvd.")
        return
    }

    blurayPath?.let {
        val player = Player(it, DiscType.BLURAY)
        player.listTitles()
        
        val thread = Thread { player.playTitle(0) }
        thread.start()
        
        runInteractiveLoop(player)
        
        try {
            thread.join()
        } catch (e: InterruptedException) {
            logger.error("Playback thread interrupted", e)
        }
        player.close()
    }

    dvdPath?.let {
        val player = Player(it, DiscType.DVD)
        player.listTitles()
        
        val thread = Thread { player.playTitle(0) }
        thread.start()
        
        runInteractiveLoop(player)
        
        try {
            thread.join()
        } catch (e: InterruptedException) {
            logger.error("Playback thread interrupted", e)
        }
        player.close()
    }

    logger.info("phizz finished")
}

fun runInteractiveLoop(player: Player) {
    val scanner = java.util.Scanner(System.`in`)
    logger.info("Controls: w=UP, s=DOWN, a=LEFT, d=RIGHT, e=ENTER, q=QUIT")
    
    var keepRunning = true
    while (keepRunning) {
        if (scanner.hasNext()) {
            val input = scanner.next()
            for (char in input) {
                when (char) {
                    'w' -> player.sendKey(LibBluRay.BD_VK_UP)
                    's' -> player.sendKey(LibBluRay.BD_VK_DOWN)
                    'a' -> player.sendKey(LibBluRay.BD_VK_LEFT)
                    'd' -> player.sendKey(LibBluRay.BD_VK_RIGHT)
                    'e' -> player.sendKey(LibBluRay.BD_VK_ENTER)
                    'q' -> {
                        keepRunning = false
                        player.stop()
                    }
                }
            }
        } else {
            keepRunning = false
        }
    }
}

fun printUsage() {
    println("Usage: phizz [options]")
    println("Options:")
    println("  --bluray <path>  Play a Blu-ray ISO or folder")
    println("  --dvd <path>     Play a DVD ISO or folder")
    println("  --server         Start in server mode")
    println("  --help, -h       Show this help message")
}
