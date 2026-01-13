package com.se122.interactivelearning.domain.model

data class AuthResponse(
    val accessToken: String? = null,
    val refreshToken: String? = null
)