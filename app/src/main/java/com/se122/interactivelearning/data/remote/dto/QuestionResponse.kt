package com.se122.interactivelearning.data.remote.dto

data class QuestionResponse(
    val id: String,
    val content: String,
    val timeLimit: Int,
    val type: String,
    val score: Int,
    val questionOption: List<QuestionOption>,
    val questionEssay: List<QuestionEssay>,
    val deletedAt: String?
)

data class QuestionOption(
    val id: String,
    val option: String,
    val isCorrect: Boolean,
    val deletedAt: String?
)

data class QuestionEssay(
    val id: String,
    val correctAnswer: String,
    val deletedAt: String?
)