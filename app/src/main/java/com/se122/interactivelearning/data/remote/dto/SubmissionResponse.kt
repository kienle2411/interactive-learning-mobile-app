package com.se122.interactivelearning.data.remote.dto

data class SubmissionResponse(
    val id: String,
    val status: SubmissionStatus,
    val score: Int?,
    val feedback: String?,
    val submittedAt: String?,
    val updatedAt: String?,
    val deletedAt: String?,
    val assignmentId: String,
    val assignment: AssignmentResponse,
    val studentId: String,
    val student: StudentResponse,
    val fileId: String?,
    val file: FileResponse?,
    val submissionFile: List<SubmissionFile>?,
    val answers: List<AssignmentAnswerResponse>?
)

enum class SubmissionStatus {
    SUBMITTED,
    GRADED
}

data class SubmissionFile(
    val id: String,
    val uploadedAt: String,
    val submissionId: String,
    val submission: SubmissionResponse,
    val fileId: String,
    val file: FileResponse
)

data class AssignmentAnswerResponse(
    val id: String,
    val submissionId: String,
    val questionId: String,
    val selectedOptionId: String?,
    val textAnswer: String?,
    val fileId: String?,
    val isCorrect: Boolean?,
    val timeSpent: Int?,
    val score: Int?,
    val feedback: String?,
    val question: AssignmentAnswerQuestion
)

data class AssignmentAnswerQuestion(
    val id: String,
    val content: String?,
    val type: String,
    val options: List<AssignmentAnswerOption>
)

data class AssignmentAnswerOption(
    val id: String,
    val content: String,
    val isCorrect: Boolean
)
