package com.se122.interactivelearning.data.remote.dto

data class SessionResponse(
    val id: String,
    val title: String,
    val description: String?,
    val startTime: String,
    val endTime: String,
    val createdAt: String,
    val updatedAt: String?,
    val deletedAt: String?,
    val fileId: String?,
    val file: FileResponse?,
)