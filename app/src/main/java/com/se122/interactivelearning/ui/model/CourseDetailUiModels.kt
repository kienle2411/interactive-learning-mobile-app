package com.se122.interactivelearning.ui.model

import com.se122.interactivelearning.data.remote.dto.AssignmentType

data class SessionUi(
    val id: String,
    val title: String,
    val description: String?,
    val startText: String,
    val endText: String,
    val canJoin: Boolean,
)

data class AssignmentUi(
    val id: String,
    val title: String,
    val description: String,
    val statusText: String,
    val status: AssignmentStatus,
    val isOngoing: Boolean,
    val startText: String,
    val dueText: String,
    val type: AssignmentType,
)

enum class AssignmentStatus {
    UPCOMING,
    ONGOING,
    ENDED
}
