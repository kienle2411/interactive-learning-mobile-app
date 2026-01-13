package com.se122.interactivelearning.data.remote.dto

data class LessonDetailResponse(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: String,
    val updatedAt: String?,
    val deletedAt: String?,
    val classroomId: String,
    val topicId: String?,
)
