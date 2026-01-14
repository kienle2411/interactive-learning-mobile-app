package com.se122.interactivelearning.data.remote.dto

data class SlideQuestionResponse(
    val id: String,
    val slideQuestions: List<SlideQuestionItem> = emptyList()
)

data class SlideQuestionItem(
    val id: String,
    val slidePageId: String,
    val questionId: String,
    val showAtSec: Int?,
    val question: QuestionResponse
)
