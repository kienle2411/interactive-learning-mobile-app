package com.se122.interactivelearning.data.remote.dto

data class ProfileResponse(
    val id: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val email: String,
    val phone: String,
    val school: String,
    val gender: String,
    val profileDescription: String,
    val avatarUrl: String,
    val role: String
)