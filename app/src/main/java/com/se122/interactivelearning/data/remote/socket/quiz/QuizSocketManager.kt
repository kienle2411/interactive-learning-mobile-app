package com.se122.interactivelearning.data.remote.socket.quiz

import android.util.Log
import com.google.gson.Gson
import com.se122.interactivelearning.data.remote.dto.UserResponse
import com.se122.interactivelearning.data.remote.socket.base.BaseSocketManager
import com.se122.interactivelearning.domain.model.QuizLeaderboardEntry
import com.se122.interactivelearning.utils.TokenManager
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizSocketManager @Inject constructor(
    tokenManager: TokenManager
): BaseSocketManager(tokenManager) {

    override val namespaceUrl: String = "ws://192.168.2.1:3001/api/quiz"

    fun joinQuiz(quizId: String) {
        emit("joinQuiz", quizId)
        Log.d(TAG, "joinQuiz: $quizId")
    }

    fun submitAnswer(quizId: String,questionId: String, optionId: String) {
        val json = JSONObject().apply {
            put("quizId", quizId)
            put("questionId", questionId)
            put("optionId", optionId)
        }
        emit("submitAnswer", json)
        Log.d(TAG, "submitAnswer: $quizId, $questionId, $optionId")
    }

    fun onReceiveQuestion(callback: (questionId: String) -> Unit) {
        off("receiveQuestion")
        on("receiveQuestion") { args ->
            Log.d(TAG, "onReceiveQuestion")
            val data = args.getOrNull(0) as? JSONObject
            val question = data?.getString("question")
            if (question != null) {
                callback(question)
            }
        }
    }

    fun onQuizStarted(callback: () -> Unit) {
        off("quizStarted")
        on("quizStarted") {
            Log.d(TAG, "onQuizStarted: ")
            callback()
        }
    }

    fun onUpdateLeaderboard(callback: (List<QuizLeaderboardEntry>) -> Unit) {
        off("updateLeaderboard")
        on("updateLeaderboard") { args ->
            Log.d(TAG, "onUpdateLeaderboard: ")
            val data = args.getOrNull(0) as? JSONArray ?: return@on
            val leaderboard = mutableListOf<QuizLeaderboardEntry>()
            for (i in 0 until data.length()) {
                val item = data.getJSONObject(i)
                val entry = QuizLeaderboardEntry(
                    studentId = item.getString("studentId"),
                    username = item.getString("username"),
                    score = item.getInt("score")
                )
                leaderboard.add(entry)
            }
            callback(leaderboard)
        }
    }

    fun onQuizEnded(callback: (message: String) -> Unit) {
        off("quizEnded")
        on("quizEnded") { args ->
            val data = args.getOrNull(0) as? JSONObject
            val message = data?.getString("message")
            if (message != null) {
                callback(message)
            }
        }
    }

    fun onRoomJoined(callback: (socketId: String, user: UserResponse) -> Unit) {
        off("roomJoined")
        on("roomJoined") {
            Log.d(TAG, "onRoomJoined: ")
            val data = it.getOrNull(0) as? JSONArray
            for (i in 0 until data!!.length()) {
                val item = data.getJSONObject(i)
                val socketId = item.getString("id")
                Log.d(TAG, "onRoomJoined: $socketId")
                val userJson = item.getJSONObject("user")
                val user = Gson().fromJson(userJson.toString(), UserResponse::class.java)
                callback(socketId, user)
            }
        }
    }

    companion object {
        private const val TAG = "QuizSocketManager"
    }
}