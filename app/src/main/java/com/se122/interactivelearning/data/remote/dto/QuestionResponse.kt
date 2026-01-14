package com.se122.interactivelearning.data.remote.dto

import com.google.gson.annotations.SerializedName

data class QuestionResponse(
    val id: String,
    val content: String? = null,
    val timeLimit: Int? = 0,
    val type: String,
    val score: Int? = 0,
    @SerializedName(value = "options", alternate = ["questionOption"])
    val options: List<QuestionOption> = emptyList(),
    val modelAnswer: String? = null,
    val rubricJson: String? = null,
    val deletedAt: String? = null
)

data class QuestionOption(
    val id: String,
    @SerializedName(value = "content", alternate = ["option"])
    val content: String,
    val isCorrect: Boolean = false,
    val orderIndex: Int? = null,
    val deletedAt: String? = null
)
