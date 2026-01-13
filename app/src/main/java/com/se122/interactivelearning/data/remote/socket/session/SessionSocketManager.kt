package com.se122.interactivelearning.data.remote.socket.session

import android.util.Log
import com.se122.interactivelearning.data.remote.socket.base.BaseSocketManager
import com.se122.interactivelearning.domain.model.ChatMessage
import com.se122.interactivelearning.domain.model.ChatMessageSession
import com.se122.interactivelearning.domain.model.SessionParticipant
import com.se122.interactivelearning.utils.TokenManager
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONArray
import org.json.JSONObject
import java.net.URISyntaxException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionSocketManager @Inject constructor(
    tokenManager: TokenManager
): BaseSocketManager(tokenManager) {
    override val namespaceUrl: String = "ws://192.168.1.42:3001/api/session"

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

    fun onSessionInfoReceived(callback: (participants: List<SessionParticipant>) -> Unit) {
        off("sessionInfo")
        on("sessionInfo") { args ->
            val data = args.getOrNull(0) as? JSONObject
            val clients = data?.optJSONArray("currentClients")
            callback(parseParticipants(clients))
        }
    }

    fun onUserJoined(
        callback: (participant: SessionParticipant, currentClients: List<SessionParticipant>?) -> Unit
    ) {
        off("userJoined")
        on("userJoined") { args ->
            val data = args.getOrNull(0) as? JSONObject
            val participantId = data?.optString("id") ?: return@on
            val participantName = data.optString("senderName", "")
            val clients = data.optJSONArray("currentClients")
            val participants = parseParticipants(clients)
            callback(
                SessionParticipant(participantId, participantName.ifBlank { participantId }),
                participants.takeIf { it.isNotEmpty() }
            )
        }
    }

    fun onUserLeft(callback: (participantId: String) -> Unit) {
        off("userLeft")
        on("userLeft") { args ->
            val data = args.getOrNull(0) as? JSONObject
            val participantId = data?.optString("id") ?: return@on
            callback(participantId)
        }
    }

    companion object {
        private const val TAG = "SessionSocketManager"
    }

    private fun parseParticipants(clients: JSONArray?): List<SessionParticipant> {
        val participants = mutableListOf<SessionParticipant>()
        if (clients == null) {
            return participants
        }
        for (index in 0 until clients.length()) {
            val item = clients.get(index)
            when (item) {
                is JSONObject -> {
                    val id = item.optString("id")
                    if (id.isNotBlank()) {
                        val name = item.optString("senderName", id)
                        participants.add(SessionParticipant(id, if (name.isBlank()) id else name))
                    }
                }
                else -> {
                    val id = clients.optString(index)
                    if (id.isNotBlank()) {
                        participants.add(SessionParticipant(id, id))
                    }
                }
            }
        }
        return participants
    }
}
