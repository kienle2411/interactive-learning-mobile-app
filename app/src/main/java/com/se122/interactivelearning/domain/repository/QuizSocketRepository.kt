package com.se122.interactivelearning.domain.repository

import kotlinx.coroutines.flow.SharedFlow
import org.json.JSONObject

interface QuizSocketRepository {
    val startEvent: SharedFlow<Unit>
    fun connect(onConnected: () -> Unit)
    fun disconnect()

    fun joinQuiz(quizId: String)
    fun submitAnswer(quizId: String, questionId: String, answer: String)

    fun onReceiveQuestion(callback: (questionId: String) -> Unit)
    fun onUpdateLeaderboard(callback: (leaderboard: JSONObject) -> Unit)
    fun onQuizEnded(callback: (message: String) -> Unit)
}