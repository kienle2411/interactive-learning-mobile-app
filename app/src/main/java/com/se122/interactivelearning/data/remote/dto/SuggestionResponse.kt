package com.se122.interactivelearning.data.remote.dto

data class SuggestionsResponse(
    val overallMastery: Double? = null,
    val suggestions: List<SuggestedActionResponse> = emptyList()
)

data class SuggestedActionResponse(
    val type: String,
    val targetId: String,
    val title: String,
    val subtitle: String? = null,
    val reason: String,
    val priority: Int,
)
