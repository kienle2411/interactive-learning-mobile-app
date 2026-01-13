package com.se122.interactivelearning.ui.screens.lesson

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.ui.components.chat.AgentChatFloating
import com.se122.interactivelearning.ui.components.chat.AgentChatViewModel
import com.se122.interactivelearning.ui.components.chat.ChatContext
import com.se122.interactivelearning.ui.components.chat.ChatScopeType
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonDetailScreen(
    id: String,
    viewModel: LessonDetailViewModel = hiltViewModel(),
    chatViewModel: AgentChatViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onQuizClick: (String) -> Unit,
) {
    val lessonDetail by viewModel.lessonDetail.collectAsState()
    val lessonQuizzes by viewModel.lessonQuizzes.collectAsState()
    val chatState by chatViewModel.uiState.collectAsState()
    var showChat by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(id) {
        viewModel.loadLesson(id)
        viewModel.loadQuizzes(id)
    }

    LaunchedEffect(id, lessonDetail) {
        val lesson = (lessonDetail as? ViewState.Success)?.data
        val title = lesson?.title?.let { "Trợ lý bài học: $it" } ?: "Trợ lý bài học"
        chatViewModel.setContext(
            ChatContext(
                scopeType = ChatScopeType.LESSON,
                classroomId = lesson?.classroomId,
                lessonId = lesson?.id,
                title = title
            )
        )
    }

    when (lessonDetail) {
        is ViewState.Error -> {
            val msg = (lessonDetail as ViewState.Error).message ?: "Unknown error"
            Text(msg, color = MaterialTheme.colorScheme.error)
        }
        is ViewState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is ViewState.Success -> {
            val lesson = (lessonDetail as ViewState.Success).data
            val html = if (lesson.content.contains("<html", ignoreCase = true)) {
                lesson.content
            } else {
                """
                <html>
                  <head>
                    <style>
                      body { font-family: serif; line-height: 1.6; padding: 16px; }
                      h1, h2, h3 { margin-top: 1.2em; }
                      img { max-width: 100%; height: auto; }
                    </style>
                  </head>
                  <body>
                    ${lesson.content}
                  </body>
                </html>
                """.trimIndent()
            }
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                        Text(
                            text = lesson.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            AndroidView(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 300.dp),
                                factory = { ctx ->
                                    WebView(ctx).apply {
                                        webViewClient = WebViewClient()
                                        settings.javaScriptEnabled = false
                                        tag = html
                                        loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
                                    }
                                },
                                update = { webView ->
                                    if (webView.tag != html) {
                                        webView.tag = html
                                        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
                                    }
                                }
                            )
                        }
                        item {
                            Text(
                                text = "Quizzes",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                        when (lessonQuizzes) {
                            is ViewState.Success -> {
                                val quizzes = (lessonQuizzes as ViewState.Success).data
                                if (quizzes.isEmpty()) {
                                    item {
                                        Text(
                                            text = "No quizzes",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                } else {
                                    items(
                                        items = quizzes,
                                        key = { it.id }
                                    ) { quiz ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                            onClick = { onQuizClick(quiz.id) }
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(12.dp)
                                            ) {
                                                Text(
                                                    text = quiz.title,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                if (!quiz.description.isNullOrBlank()) {
                                                    Text(
                                                        text = quiz.description ?: "",
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            is ViewState.Loading -> {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                            is ViewState.Error -> {
                                val msg = (lessonQuizzes as ViewState.Error).message ?: "Unknown error"
                                item {
                                    Text(msg, color = MaterialTheme.colorScheme.error)
                                }
                            }
                            is ViewState.Idle -> {}
                        }
                    }
                }

                AgentChatFloating(
                    isOpen = showChat,
                    title = "Trợ lý bài học",
                    messages = chatState.messages,
                    isSending = chatState.isSending,
                    isLoading = chatState.isLoading,
                    errorMessage = chatState.errorMessage,
                    onToggle = {
                        showChat = !showChat
                        if (showChat) {
                            chatViewModel.ensureThread()
                        }
                    },
                    onSend = { chatViewModel.sendMessage(it) },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                )
            }
        }
        is ViewState.Idle -> {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
