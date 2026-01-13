package com.se122.interactivelearning.domain.model

import io.livekit.android.room.participant.Participant

data class ChatMessage(
    val participant: Participant?,
    val message: String,
    val senderName: String,
    val timestamp: Long,
)

data class ChatMessageSession(
    val id: String,
    val senderName: String,
    val message: String,
    val timestamp: Long
)