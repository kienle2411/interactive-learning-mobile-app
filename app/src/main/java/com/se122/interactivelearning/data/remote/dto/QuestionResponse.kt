package com.se122.interactivelearning.data.remote.dto

data class QuestionData(
    val id: String,
    val content: String,
    val timeLimit: Int,
    val score: Int,
    val questionOption: List<QuestionOption>,
    val deletedAt: String?
)

data class QuestionOption(
    val id: String,
    val option: String,
    val isCorrect: Boolean,
    val deletedAt: String? // null if not deleted
)
