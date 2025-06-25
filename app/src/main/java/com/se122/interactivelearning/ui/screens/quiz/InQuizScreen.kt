package com.se122.interactivelearning.ui.screens.quiz

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.se122.interactivelearning.ui.components.MultipleChoiceQuestion
import androidx.compose.runtime.getValue
import com.se122.interactivelearning.common.ViewState

@Composable
fun InQuizScreen(
    inQuizViewModel: InQuizViewModel = hiltViewModel(),
    quizId: String,
    onBackClick: () -> Unit
) {
    val question by inQuizViewModel.question.collectAsState()

    LaunchedEffect(Unit) {
        inQuizViewModel.connectSocket()
        inQuizViewModel.joinQuiz(quizId)
    }

    Column {
        Text(
            text = question?.content ?: "Loading..."
        )
        question?.let {
            MultipleChoiceQuestion(
                questionData = it
            )
        }
    }
}