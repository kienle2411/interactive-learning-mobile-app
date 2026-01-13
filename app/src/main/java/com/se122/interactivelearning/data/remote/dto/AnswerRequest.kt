package com.se122.interactivelearning.data.remote.dto

data class AnswerRequest(
    val contextId: String,
    val questionId: String,
    val type: AnswerType,
    val text: String?,
    val selectedOptionId: String?,
    val answerSource: AnswerSource
)

enum class AnswerType {
    QUIZ,
    SUBMISSION
}

data class AnswerSource(
    val type: String,
    val contextId: String
)