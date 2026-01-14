package com.se122.interactivelearning.data.remote.dto

data class QuizAttemptQuestionItem(
    val quizQuestionId: String,
    val orderIndex: Int,
    val points: Int,
    val question: QuizQuestionDetail,
    val answer: QuizAnswerSnapshot?
)

data class QuizQuestionDetail(
    val id: String,
    val content: String?,
    val type: String,
    val options: List<QuizQuestionOption>
)

data class QuizQuestionOption(
    val id: String,
    val content: String,
    val isCorrect: Boolean
)

data class QuizAnswerSnapshot(
    val questionId: String,
    val selectedOptionId: String?,
    val textAnswer: String?,
    val isCorrect: Boolean?,
    val score: Int?,
    val feedback: String?
)
