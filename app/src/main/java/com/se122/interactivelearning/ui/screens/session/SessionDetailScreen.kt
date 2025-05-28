package com.se122.interactivelearning.ui.screens.session

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.se122.interactivelearning.data.remote.dto.SessionResponse

@Composable
fun SessionDetailScreen(
    sessionResponse: SessionResponse
) {
    Column {
        Text(
            text = sessionResponse.title,
            style = MaterialTheme.typography.titleLarge
        )
    }
    // Slide Share Screen

    // Question Session

    // Chat Session

}