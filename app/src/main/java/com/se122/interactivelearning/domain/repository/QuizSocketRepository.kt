package com.se122.interactivelearning.domain.repository

import com.se122.interactivelearning.data.remote.dto.UserResponse
import org.json.JSONObject

interface QuizSocketRepository {
    fun connect(onConnected: () -> Unit)
    fun disconnect()

    fun joinQuiz(quizId: String)
    fun submitAnswer(quizId: String, questionId: String, answer: String)

    fun onQuizStarted(callback: () -> Unit)
    fun onReceiveQuestion(callback: (questionId: String) -> Unit)
    fun onUpdateLeaderboard(callback: (leaderboard: JSONObject) -> Unit)
    fun onQuizEnded(callback: (message: String) -> Unit)
    fun onRoomJoined(callback: (socketId: String, user: UserResponse) -> Unit)
}