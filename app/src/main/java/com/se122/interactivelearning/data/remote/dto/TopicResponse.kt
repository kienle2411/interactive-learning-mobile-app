package com.se122.interactivelearning.data.remote.dto

data class TopicResponse(
    val id: String,
    val name: String,
    val description: String?,
    val classroomId: String?,
    val createdAt: String,
    val updatedAt: String?,
    val deletedAt: String?,
)

data class LessonResponse(
    val id: String,
    val title: String,
    val createdAt: String,
    val updatedAt: String?,
    val deletedAt: String?,
)
