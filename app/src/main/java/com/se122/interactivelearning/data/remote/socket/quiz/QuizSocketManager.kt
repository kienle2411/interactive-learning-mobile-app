package com.se122.interactivelearning.data.remote.socket.quiz

import android.util.Log
import com.se122.interactivelearning.data.remote.socket.base.BaseSocketManager
import com.se122.interactivelearning.utils.TokenManager
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizSocketManager @Inject constructor(
    tokenManager: TokenManager
): BaseSocketManager(tokenManager) {

    override val namespaceUrl: String = "ws://192.168.1.7:3001/api/quiz"

    fun joinQuiz(quizId: String) {
        emit("joinQuiz", quizId)
        Log.d(TAG, "joinQuiz: $quizId")
    }

    fun submitAnswer(quizId: String,questionId: String, answer: String) {
        val json = JSONObject().apply {
            put("quizId", quizId)
            put("questionId", questionId)
            put("answer", answer)
        }
        emit("submitAnswer", json)
        Log.d(TAG, "submitAnswer: $quizId, $questionId, $answer")
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

    fun onUpdateLeaderBoard(callback: (leaderboard: JSONObject) -> Unit) {
        off("updateLeaderBoard")
        on("updateLeaderBoard") { args ->
            val data = args.getOrNull(0) as? JSONObject
            data?.let { callback(it) }
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

    companion object {
        private const val TAG = "QuizSocketManager"
    }
}