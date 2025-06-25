package com.se122.interactivelearning.data.remote.dto

data class RegisterRequest(
    val username: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val email: String,
    val phone: String,
    val gender: String,
    val role: String = "STUDENT"
)