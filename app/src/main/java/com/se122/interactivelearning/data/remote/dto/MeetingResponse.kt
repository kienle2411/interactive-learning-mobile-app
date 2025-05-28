package com.se122.interactivelearning.data.remote.dto

data class MeetingResponse(
    val id: String,
    val title: String,
    val description: String?,
    val recordUrl: String?,
    val startTime: String,
    val endTime: String,
    val createdAt: String,
    val updatedAt: String?,
    val deletedAt: String?,
    val createdBy: String?,
    val classroomId: String,
    val hostId: String?,
    val host: TeacherResponse,
)

data class TeacherResponse(
    val id: String,
    val subjectSpecialization: String?,
    val user: UserResponse,
)