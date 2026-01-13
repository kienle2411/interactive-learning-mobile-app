package com.se122.interactivelearning.domain.model

data class QuizLeaderboardEntry(
    val studentId: String,
    val username: String,
    val score: Int
)