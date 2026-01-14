package com.se122.interactivelearning.data.remote.dto

data class SubmitQuizAnswerRequest(
    val questionId: String,
    val selectedOptionId: String? = null,
    val textAnswer: String? = null,
    val fileId: String? = null,
    val timeSpent: Int? = null
)
