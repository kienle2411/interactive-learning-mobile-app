package com.se122.interactivelearning.ui.screens.quiz

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import com.se122.interactivelearning.R
import com.se122.interactivelearning.common.ViewState

@Composable
fun QuizListScreen(
    quizListViewModel: QuizListViewModel = hiltViewModel()
) {
    val attemptQuiz by quizListViewModel.attemptQuiz.collectAsState()

    val quizCode = remember { mutableStateOf(TextFieldValue("")) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = quizCode.value,
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
                        quizListViewModel.attemptQuiz(quizCode.value.toString())
                    }
                ) {
                    when (attemptQuiz) {
                        is ViewState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        else -> {
                            Text("Join")
                        }
                    }
                }
            }
        }
    }
}