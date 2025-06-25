package com.se122.interactivelearning.ui.screens.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.se122.interactivelearning.R
import com.se122.interactivelearning.common.ViewState

@Composable
fun QuizListScreen(
    quizListViewModel: QuizListViewModel = hiltViewModel(),
    onJoin: (String) -> Unit
) {
    val attemptQuiz by quizListViewModel.attemptQuiz.collectAsState()

    val quizCode = remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(attemptQuiz) {
        if (attemptQuiz is ViewState.Success) {
            val quizId = (attemptQuiz as ViewState.Success).data.quizId
            onJoin(quizId)
            quizListViewModel.resetAttemptQuiz()
        }
    }

    Scaffold(
        modifier = Modifier.padding(20.dp)
    ){ innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = quizCode.value,
                    modifier = Modifier.weight(5f),
                    onValueChange = {
                        quizCode.value = it
                    },
                    label = {
                        Text("Quiz Code")
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {}
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_qr_scan),
                                contentDescription = "Search"
                            )
                        }
                    }
                )
                Button(
                    onClick = {
                        if (quizCode.value.text.isNotEmpty()) {
                            quizListViewModel.attemptQuiz(quizCode.value.text)
                        }
                    },
                    modifier = Modifier.weight(2f).height(56.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    when (attemptQuiz) {
                        is ViewState.Loading -> {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.surface,
                            )
                        }
                        else -> {
                            Text(
                                text = "Join",
                            )
                        }
                    }
                }
            }
        }
    }
}