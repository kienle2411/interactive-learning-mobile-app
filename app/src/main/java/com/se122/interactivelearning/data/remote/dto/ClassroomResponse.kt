package com.se122.interactivelearning.data.remote.dto

data class ClassroomWrapperResponse(
    val classroom: ClassroomResponse
)

data class ClassroomResponse(
    val id: String,
    val name: String,
    val code: String,
    val description: String,
    val capacity: Int,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String,
    val teacher: TeacherClassroomResponse,
)

data class TeacherClassroomResponse(
    val user: UserTeacherResponse,
)

data class UserTeacherResponse(
    val firstName: String,
    val lastName: String,
)

data class ClassroomDetailsResponse(
    val id: String,
    val name: String,
    val code: String,
    val description: String,
    val capacity: Int,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String,
)