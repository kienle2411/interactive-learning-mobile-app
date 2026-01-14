package com.se122.interactivelearning.data.remote.dto

data class QuizAnswerResponse(
    val id: String,
    val quizAttemptId: String,
    val questionId: String,
    val selectedOptionId: String?,
    val textAnswer: String?,
    val fileId: String?,
    val isCorrect: Boolean?,
    val timeSpent: Int?,
    val score: Int?,
    val feedback: String?
)
