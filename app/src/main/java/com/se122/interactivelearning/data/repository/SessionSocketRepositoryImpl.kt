package com.se122.interactivelearning.data.repository

import com.se122.interactivelearning.data.remote.socket.session.SessionSocketManager
import com.se122.interactivelearning.domain.model.ChatMessageSession
import com.se122.interactivelearning.domain.repository.SessionSocketRepository
import javax.inject.Inject

class SessionSocketRepositoryImpl @Inject constructor(
    private val sessionSocketManager: SessionSocketManager
): SessionSocketRepository {
    override fun connect(onConnected: () -> Unit) {
        sessionSocketManager.connect(onConnected)
    }

    override fun joinSession(sessionId: String) {
        sessionSocketManager.joinSession(sessionId)
    }

    override fun onSlideReceived(callback: (slideUrl: String, slidePageId: String) -> Unit) {
        sessionSocketManager.onSlideReceived(callback)
    }

    override fun onMessageReceived(callback: (ChatMessageSession) -> Unit) {
        sessionSocketManager.onMessageReceived(callback)
    }

    override fun onQuestionReceived(callback: (questionId: String) -> Unit) {
        sessionSocketManager.onQuestionReceived(callback)
    }

    override fun sendMessage(sessionId: String, message: String) {
        sessionSocketManager.sendMessage(sessionId, message)
    }

    override fun leaveSession(sessionId: String) {
        sessionSocketManager.leaveSession(sessionId)
    }

    override fun disconnect() {
        sessionSocketManager.disconnect()
    }
}