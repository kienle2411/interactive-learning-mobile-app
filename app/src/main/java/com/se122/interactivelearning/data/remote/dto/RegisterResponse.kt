package com.se122.interactivelearning.data.remote.dto

data class RegisterResponse(
    val accessToken: String,
    val refreshToken: String
)