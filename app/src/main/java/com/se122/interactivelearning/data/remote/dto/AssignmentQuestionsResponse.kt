package com.se122.interactivelearning.data.remote.dto

data class AssignmentQuestionsResponse(
    val id: String,
    val title: String,
    val description: String,
    val startTime: String,
    val dueTime: String,
    val type: AssignmentType,
    val classroomId: String,
    val assignmentQuestions: List<AssignmentQuestionItem>
)

data class AssignmentQuestionItem(
    val id: String,
    val orderIndex: Int,
    val points: Int,
    val question: AssignmentQuestionDetail
)

data class AssignmentQuestionDetail(
    val id: String,
    val content: String?,
    val type: String,
    val options: List<AssignmentQuestionOption>
)

data class AssignmentQuestionOption(
    val id: String,
    val content: String,
    val isCorrect: Boolean
)
