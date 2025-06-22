package com.se122.interactivelearning.domain.repository

import org.json.JSONObject

interface QuizSocketRepository {
    fun connect(onConnected: () -> Unit)
    fun disconnect()

    fun joinQuiz(quizId: String)
    fun submitAnswer(quizId: String, questionId: String, answer: String)

    fun onReceiveQuestion(callback: (question: String, startAt: String) -> Unit)
    fun onUpdateLeaderboard(callback: (leaderboard: JSONObject) -> Unit)
    fun onQuizEnded(callback: (message: String) -> Unit)
}