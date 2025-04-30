package com.se122.interactivelearning.ui.screens.course

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun CourseScreen() {
    Text(
        text = "Course Screen",
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface

    )
}