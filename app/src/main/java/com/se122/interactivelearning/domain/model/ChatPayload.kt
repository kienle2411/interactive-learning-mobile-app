package com.se122.interactivelearning.domain.model

data class ChatPayload(
    val senderId: String,
    val senderName: String,
    val message: String,
)