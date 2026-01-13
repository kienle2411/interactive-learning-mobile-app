package com.se122.interactivelearning.domain.model

import java.util.Date

data class User(
    val id: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: Date,
    val email: String,
    val phone: String,
    val school: String,
    val gender: String,
    val profileDescription: String?,
    val createdAt: Date,
    val updatedAt: Date?,
    val avatarUrl: String?,
    val role: String
)