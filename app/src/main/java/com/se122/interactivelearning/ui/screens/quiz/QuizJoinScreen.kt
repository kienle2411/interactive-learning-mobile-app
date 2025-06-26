package com.se122.interactivelearning.ui.screens.quiz

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.se122.interactivelearning.R
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import coil3.compose.AsyncImage
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.ui.components.StudentCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizJoinScreen(
    quizId: String,
    quizJoinViewModel: QuizJoinViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onJoinClick: () -> Unit,
    onStart: () -> Unit
) {
    val quiz by quizJoinViewModel.quiz.collectAsState()
    val start by quizJoinViewModel.start.collectAsState()
    val userList by quizJoinViewModel.userList.collectAsState()

    LaunchedEffect(start) {
        if (start) {
            onStart.invoke()
            quizJoinViewModel.resetStart()
        }
    }

    LaunchedEffect(Unit) {
        quizJoinViewModel.getQuiz(quizId)
        quizJoinViewModel.connectSocket()
        quizJoinViewModel.joinQuiz(quizId)
        quizJoinViewModel.observeRoomJoined()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = WindowInsets(0),
                title = {
                    Text(
                        text =  if (quiz is ViewState.Success) (quiz as ViewState.Success).data.title else "",
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBackClick.invoke()
                            quizJoinViewModel.disconnect()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                windowInsets = WindowInsets(0)
            ) {
                Button(
                    onClick = {}
                ) {
                    Text(text = "Join")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxWidth().padding(innerPadding).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Waiting for players to join..."
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                userList.forEach {
                    Card(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = it.avatarUrl ?: R.drawable.img_avatar,
                                contentDescription = "Avatar",
                                modifier = Modifier.size(50.dp)
                            )
                            Text(
                                text = it.firstName + " " + it.lastName,
                                modifier = Modifier.padding(horizontal = 5.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}