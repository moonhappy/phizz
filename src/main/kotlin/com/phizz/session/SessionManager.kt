package com.phizz.session

import com.phizz.player.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class SessionManager {
    private val sessions = ConcurrentHashMap<String, Player>()

    fun createSession(): String {
        val sessionId = UUID.randomUUID().toString()
        val player = Player()
        sessions[sessionId] = player
        return sessionId
    }

    fun getSession(sessionId: String): Player? {
        return sessions[sessionId]
    }

    fun stopSession(sessionId: String): Boolean {
        val player = sessions.remove(sessionId)
        player?.stop()
        return player != null
    }

    fun stopAllSessions() {
        sessions.keys.forEach { sessionId ->
            stopSession(sessionId)
        }
    }
}
