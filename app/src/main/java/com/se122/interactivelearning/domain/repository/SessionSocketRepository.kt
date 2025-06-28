package com.se122.interactivelearning.domain.repository

import com.se122.interactivelearning.domain.model.ChatMessageSession

interface SessionSocketRepository {
    fun connect(onConnected: () -> Unit)
    fun disconnect()
    fun joinSession(sessionId: String)
    fun leaveSession(sessionId: String)
    fun sendMessage(sessionId: String, message: String)
    fun onSlideReceived(callback: (slideUrl: String, slidePageId: String) -> Unit)
    fun onMessageReceived(callback: (chatMessage: ChatMessageSession) -> Unit)
    fun onQuestionReceived(callback: (questionId: String) -> Unit)
}