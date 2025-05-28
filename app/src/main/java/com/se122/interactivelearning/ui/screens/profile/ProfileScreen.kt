package com.se122.interactivelearning.ui.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.se122.interactivelearning.ui.screens.meeting.MeetingJoinScreen

@Composable
fun ProfileScreen() {
    Column {
        Text(
            text = "Profile Screen",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        MeetingJoinScreen()
    }
}
