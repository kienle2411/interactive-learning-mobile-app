package com.se122.interactivelearning.data.remote.socket.session

import android.util.Log
import com.se122.interactivelearning.data.remote.socket.base.BaseSocketManager
import com.se122.interactivelearning.domain.model.ChatMessage
import com.se122.interactivelearning.domain.model.ChatMessageSession
import com.se122.interactivelearning.utils.TokenManager
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionSocketManager @Inject constructor(
    tokenManager: TokenManager
): BaseSocketManager(tokenManager) {
    override val namespaceUrl: String = "ws://192.168.1.6:3001/api/session"

    fun joinSession(sessionId: String) {
        emit("joinSession", sessionId)
        Log.d(TAG, "joinSession: $sessionId")
    }

    fun leaveSession(sessionId: String) {
        emit("leaveSession", sessionId)
        Log.d(TAG, "leaveSession: $sessionId")
    }

    fun sendMessage(sessionId: String, message: String) {
        val json = JSONObject().apply {
            put("sessionId", sessionId)
            put("message", message)
        }
        emit("sendMessage", json)
        Log.d(TAG, "sendMessage: $sessionId, $message")
    }

    fun onMessageReceived(callback: (chatMessage: ChatMessageSession) -> Unit) {
        off("receiveMessage")
        on("receiveMessage") { args ->
            val data = args.getOrNull(0) as? JSONObject
            val senderId = data?.getString("id") ?: ""
            val message = data?.getString("message") ?: ""
            val senderName = data?.getString("senderName") ?: ""
            val timestamp = System.currentTimeMillis()
            val chatMessage = ChatMessageSession(senderId, senderName, message, timestamp)
            callback(chatMessage)
        }
    }

    fun onSlideReceived(callback: (slideUrl: String, slidePageId: String) -> Unit) {
        off("receiveSlide")
        on("receiveSlide") { args ->
            val data = args.getOrNull(0) as? JSONObject
            val slideUrl = data?.optString("slideUrl")
            val slidePageId = data?.optString("slidePageId")
            slideUrl?.let { callback(it, slidePageId ?: "") }
            Log.d(TAG, "onSlideReceived: $slideUrl")
            Log.d(TAG, "onSlideReceived: $slidePageId")
        }
    }

    fun onQuestionReceived(callback: (questionId: String) -> Unit) {
        off("receiveQuestion")
        on("receiveQuestion") { args ->
            val data = args.getOrNull(0) as? JSONObject
            val sessionId = data?.optString("sessionId")
            val questionId = data?.optString("questionId")
            questionId?.let { callback(it) }
            Log.d(TAG, "onQuestionReceived: $questionId")
        }
    }

    companion object {
        private const val TAG = "SessionSocketManager"
    }
}