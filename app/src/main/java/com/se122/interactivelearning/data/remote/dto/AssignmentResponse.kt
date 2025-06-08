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

enum class AssignmentType {
    QUIZ,
    ASSIGNMENT
}