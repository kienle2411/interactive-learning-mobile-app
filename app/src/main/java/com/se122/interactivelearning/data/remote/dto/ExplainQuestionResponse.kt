package com.se122.interactivelearning.data.remote.dto

data class ExplainQuestionResponse(
    val questionId: String,
    val explanation: String,
    val citations: List<ExplainQuestionCitation>?
)

data class ExplainQuestionCitation(
    val documentId: String,
    val documentName: String,
    val lesson: ExplainQuestionLesson?,
    val topic: ExplainQuestionTopic?
)

data class ExplainQuestionLesson(
    val id: String,
    val title: String
)

data class ExplainQuestionTopic(
    val id: String,
    val name: String
)
