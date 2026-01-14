package com.se122.interactivelearning.data.remote.dto

data class QuizAttemptResponse(
    val id: String,
    val startTime: String,
    val endTime: String,
    val score: Int?,
    val deletedAt: String?,
    val createdAt: String,
    val updatedAt: String?,
    val studentId: String,
    val quizId: String,
)