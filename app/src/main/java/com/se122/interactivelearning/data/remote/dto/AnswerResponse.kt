package com.se122.interactivelearning.data.remote.dto

data class AnswerResponse(
    val id: String,
    val type: String,
    val text: String?,
    val selectedOptionId: String?,
    val isCorrect: Boolean,
    val score: Int,
    val questionId: String,
    val createdAt: String,
    val updatedAt: String
)