package com.se122.interactivelearning.data.remote.dto

data class StudentResponse(
    val id: String,
    val userId: String,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String,
    val user: UserResponse
)

data class ClassroomStudentResponse(
    val id: String,
    val studentId: String,
    val classroomId: String,
    val score: Int,
    val joinedAt: String,
    val leftAt: String,
    val deletedAt: String,
    val student: StudentResponse,
    val studentGroup: List<StudentGroupResponse>,
    val classroom: ClassroomResponse
)

data class StudentGroupResponse(
    val id: String,
    val studentId: String,
    val groupId: String,
    val classroomId: String,
    val createdAt: String,
    val deletedAt: String,
    val group: GroupResponse
)

data class GroupResponse(
    val id: String,
    val name: String,
    val score: String,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String,
    val classroomId: String
)