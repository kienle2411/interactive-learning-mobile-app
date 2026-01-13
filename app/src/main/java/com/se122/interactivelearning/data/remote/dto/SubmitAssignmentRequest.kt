package com.se122.interactivelearning.data.remote.dto

data class SubmitAssignmentRequest(
    val answers: List<AssignmentAnswerRequest>? = null,
    val fileIds: List<String>? = null,
    val status: SubmissionStatus? = null
)

data class AssignmentAnswerRequest(
    val questionId: String,
    val selectedOptionId: String? = null,
    val textAnswer: String? = null,
    val fileId: String? = null,
    val timeSpent: Int? = null
)
