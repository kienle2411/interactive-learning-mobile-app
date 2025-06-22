package com.se122.interactivelearning.data.remote.dto

data class QuizResponse(
    val id: String,
    val title: String,
    val code: String,
    val description: String,
    val timeLimit: Long,
    val createdAt: String,
    val updatedAt: String?,
    val deletedAt: String?,
    val teacherId: String,
)