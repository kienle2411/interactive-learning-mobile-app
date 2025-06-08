package com.se122.interactivelearning.data.remote.dto

data class UserResponse(
    val id: String,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val avatarUrl: String?
)