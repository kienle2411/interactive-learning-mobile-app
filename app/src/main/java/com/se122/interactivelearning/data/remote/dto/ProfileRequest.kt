package com.se122.interactivelearning.data.remote.dto

data class ProfileRequest(
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val email: String,
    val phone: String,
)