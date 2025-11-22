package phizz

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("phizz.Main")

fun main(args: Array<String>) {
    logger.info("phizz started")
    if (args.isEmpty()) {
        logger.error("No arguments provided. Use --bluray or --dvd.")
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
        player.playTitle(0) // Play the first title for PoC
        player.close()
    }

    dvdPath?.let {
        val player = Player(it, DiscType.DVD)
        player.listTitles()
        player.playTitle(0) // Play the first title (DVD usually starts at 1, but let's see how our logic handles it or if we just start reading)
        player.close()
    }

    logger.info("phizz finished")
}
