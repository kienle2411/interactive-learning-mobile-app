package com.se122.interactivelearning.ui.screens.quiz

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import com.se122.interactivelearning.R
import com.se122.interactivelearning.common.ViewState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizListScreen(
    quizListViewModel: QuizListViewModel = hiltViewModel(),
    onJoin: (String) -> Unit
) {
    val attemptQuiz by quizListViewModel.attemptQuiz.collectAsState()
    val quizCode = remember { mutableStateOf(TextFieldValue("")) }

    val showErrorDialog = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }

    LaunchedEffect(attemptQuiz) {
        if (attemptQuiz is ViewState.Success) {
            val quizId = (attemptQuiz as ViewState.Success).data.quizId
            onJoin(quizId)
            quizListViewModel.resetAttemptQuiz()
        }
        if (attemptQuiz is ViewState.Error) {
            errorMessage.value = (attemptQuiz as ViewState.Error).message ?: "Unknown error"
            showErrorDialog.value = true
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.quiz_img),
                    contentDescription = "Banner",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = quizCode.value,
                        modifier = Modifier
                            .weight(5f)
                            .height(56.dp),
                        onValueChange = {
                            quizCode.value = it
                        },
                        label = {
                            Text("Quiz Code")
                        },
                    )
                    Button(
                        onClick = {
                            if (quizCode.value.text.isNotEmpty()) {
                                quizListViewModel.attemptQuiz(quizCode.value.text)
                            }
                        },
                        modifier = Modifier
                            .weight(2f)
                            .height(56.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        when (attemptQuiz) {
                            is ViewState.Loading -> CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            else -> Text("Join")
                        }
                    }
                }
            }
            Box(modifier = Modifier.height(100.dp))
        }

        if (attemptQuiz is ViewState.Loading) {
            Dialog(
                onDismissRequest = {}
            ) {
                CircularProgressIndicator()
            }
        }

        if (showErrorDialog.value) {
            BasicAlertDialog(
                onDismissRequest = {
                    showErrorDialog.value = false
                },
                modifier = Modifier
                    .zIndex(1f)
                    .clip(RoundedCornerShape(5.dp))
                    .border(1.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(5.dp))
                    .background(MaterialTheme.colorScheme.background),
                content = {
                    Box {
                        Text(
                            modifier = Modifier.padding(10.dp),
                            text = errorMessage.value,
                        )
                    }
                }
            )
        }
    }
}
