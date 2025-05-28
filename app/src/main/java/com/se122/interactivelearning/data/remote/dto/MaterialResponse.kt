package com.se122.interactivelearning.data.remote.dto

data class MaterialResponse(
    val id: String,
    val title: String,
    val description: String,
    val type: String,
    val createdBy: String,
    val createdAt: String,
    val updatedAt: String,
    val classroomId: String,
    val file: List<FileResponse>
)

data class FileResponse(
    val id: String,
    val name: String,
    val url: String,
    val type: String,
    val uploadedBy: String,
    val createdAt: String,
    val deletedAt: String,
)