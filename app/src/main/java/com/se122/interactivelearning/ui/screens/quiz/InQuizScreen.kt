package com.se122.interactivelearning.ui.screens.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.se122.interactivelearning.ui.components.MultipleChoiceQuestion
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.se122.interactivelearning.common.ViewState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InQuizScreen(
    inQuizViewModel: InQuizViewModel = hiltViewModel(),
    quizId: String,
    onBackClick: () -> Unit,
    onEndedQuiz: () -> Unit
) {
    val question by inQuizViewModel.question.collectAsState()
    val timeLeft by inQuizViewModel.countdownTime.collectAsState()
    val end by inQuizViewModel.end.collectAsState()

    DisposableEffect(Unit) {
        inQuizViewModel.observeQuestion()
        inQuizViewModel.observeEndQuiz()
        onDispose {
            inQuizViewModel.disconnect()
        }
    }

    LaunchedEffect(end) {
        if (end) {
            onEndedQuiz.invoke()
            inQuizViewModel.resetEnd()
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier.shadow(elevation = 10.dp).padding(horizontal = 10.dp, vertical = 5.dp).align(Alignment.CenterHorizontally),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Time",
                            modifier = Modifier.width(50.dp)
                        )
                        LinearProgressIndicator(
                            modifier = Modifier.weight(1f),
                            progress = {
                                if (question is ViewState.Success) {
                                    timeLeft.toFloat() / (question as ViewState.Success).data.timeLimit.toFloat()
                                } else {
                                    0f
                                }
                            },
                        )
                        Text(
                            text = "${timeLeft}s",
                            modifier = Modifier.width(50.dp),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            when (question) {
                is ViewState.Success -> {
                    val question = (question as ViewState.Success).data
                    MultipleChoiceQuestion(
                        questionData = question
                    )
                }
                else -> {}
            }
        }
    }
}