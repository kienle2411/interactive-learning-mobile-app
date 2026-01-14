package com.se122.interactivelearning.ui.screens.notification

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun NotificationScreen() {
    Text(
        text = "Notification Screen",
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface

    )
}