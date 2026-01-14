package com.se122.interactivelearning.data.remote.dto

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)