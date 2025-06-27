package com.se122.interactivelearning.domain.repository

import com.se122.interactivelearning.data.remote.dto.UserResponse
import com.se122.interactivelearning.domain.model.QuizLeaderboardEntry
import org.json.JSONObject

interface QuizSocketRepository {
    fun connect(onConnected: () -> Unit)
    fun disconnect()

    fun joinQuiz(quizId: String)
    fun submitAnswer(quizId: String, questionId: String, optionId: String)

    fun onQuizStarted(callback: () -> Unit)
    fun onReceiveQuestion(callback: (questionId: String) -> Unit)
    fun onUpdateLeaderboard(callback: (leaderboard: List<QuizLeaderboardEntry>) -> Unit)
    fun onQuizEnded(callback: (message: String) -> Unit)
    fun onRoomJoined(callback: (socketId: String, user: UserResponse) -> Unit)
}