package com.se122.interactivelearning.data.remote.dto

data class AssignmentResponse(
    val id: String,
    val title: String,
    val description: String,
    val startTime: String,
    val dueTime: String,
    val createdAt: String?,
    val updatedAt: String?,
    val deletedAt: String?,
    val type: AssignmentType,
    val materialId: String?,
    val material: MaterialResponse?,
    val submission: List<SubmissionResponse>?
)

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
    val submissionFile: List<SubmissionFile>?
)

data class SubmissionFile(
    val id: String,
    val uploadedAt: String,
    val submissionId: String,
    val submission: SubmissionResponse,
    val fileId: String,
    val file: FileResponse
)

enum class SubmissionStatus {
    SUBMITTED,
    GRADED
}

enum class AssignmentType {
    QUIZ,
    ASSIGNMENT
}