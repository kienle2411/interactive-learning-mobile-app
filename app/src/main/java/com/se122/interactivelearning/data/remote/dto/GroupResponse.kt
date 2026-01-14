package com.se122.interactivelearning.data.remote.dto

data class Group(
    val id: String,
    val name: String,
    val score: Int,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?,
    val classroomId: String
)