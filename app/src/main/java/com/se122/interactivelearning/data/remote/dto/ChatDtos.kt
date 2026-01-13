package com.se122.interactivelearning.data.remote.dto

data class ChatThreadResponse(
    val id: String,
    val title: String?,
    val scopeType: String,
    val classroomId: String?,
    val lessonId: String?,
    val assignmentId: String?,
    val quizId: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val messages: List<ChatMessageResponse>? = null
)

data class ChatMessageResponse(
    val id: String,
    val threadId: String,
    val role: String,
    val content: String,
    val createdAt: String?,
    val userId: String?
)

data class CreateChatThreadRequest(
    val title: String? = null,
    val scopeType: String? = null,
    val classroomId: String? = null,
    val lessonId: String? = null,
    val assignmentId: String? = null,
    val quizId: String? = null
)

data class SendChatMessageRequest(
    val content: String,
    val questionId: String? = null,
    val documentIds: List<String>? = null,
    val topK: Int? = null
)

data class SendChatMessageResponse(
    val threadId: String,
    val userMessage: ChatMessageResponse,
    val assistantMessage: ChatMessageResponse,
    val usedDocIds: List<String>?
)
