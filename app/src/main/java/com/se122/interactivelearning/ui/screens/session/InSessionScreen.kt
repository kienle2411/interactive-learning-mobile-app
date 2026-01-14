package com.se122.interactivelearning.ui.screens.session

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.se122.interactivelearning.R
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.data.remote.dto.AnswerRequest
import com.se122.interactivelearning.data.remote.dto.AnswerSource
import com.se122.interactivelearning.data.remote.dto.AnswerType
import com.se122.interactivelearning.domain.model.ChatMessageSession
import com.se122.interactivelearning.domain.model.SessionParticipant
import com.se122.interactivelearning.ui.components.EssayQuestion
import com.se122.interactivelearning.ui.components.MessageCard
import com.se122.interactivelearning.ui.components.MultipleChoiceQuestion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InSessionScreen(
    sessionId: String,
    onLeaveClick: () -> Unit,
    inSessionViewModel: InSessionViewModel = hiltViewModel()
) {
    val session by inSessionViewModel.session.collectAsState()

    val slideUrl by inSessionViewModel.slideUrl.collectAsState()
    val slidePageId by inSessionViewModel.slidePageId.collectAsState()

    val messages by inSessionViewModel.messages.collectAsState()
    val listState = rememberLazyListState()

    val participants by inSessionViewModel.participants.collectAsState()

    val question by inSessionViewModel.question.collectAsState()
    val answer by inSessionViewModel.answer.collectAsState()

    var showChatSheet by remember { mutableStateOf(false) }
    var messageText by remember { mutableStateOf("") }

    LaunchedEffect(question) {
        Log.i("InSessionScreen", "Question: $question")
    }

    LaunchedEffect(slidePageId) {
        Log.i("InSessionScreen", "SlidePageId: $slidePageId")
    }

    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    LaunchedEffect(Unit) {
        inSessionViewModel.getSession(sessionId)
        inSessionViewModel.connectAndJoin(sessionId)
    }

    DisposableEffect(Unit) {
        onDispose {
            inSessionViewModel.leaveSession(sessionId)
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (session is ViewState.Success) (session as ViewState.Success).data.title else "",
                    style = MaterialTheme.typography.titleLarge.copy(letterSpacing = 0.2.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ParticipantCountChip(count = participants.size)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showChatSheet = true },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_chat),
                                contentDescription = "Chat"
                            )
                        }
                        Button(
                            onClick = {
                                inSessionViewModel.leaveSession(sessionId)
                                onLeaveClick()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Leave Session"
                            )
                        }
                    }
                }
            }
        },
    ) { innerPadding ->
        if (showChatSheet) {
            ChatBottomSheet(
                messages = messages,
                messageText = messageText,
                onMessageTextChange = { messageText = it },
                onSendMessage = {
                    if (messageText.isNotBlank()) {
                        inSessionViewModel.sendMessage(sessionId, messageText)
                        messageText = ""
                    }
                },
                onDismiss = { showChatSheet = false },
                listState = listState
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth().padding(innerPadding).padding(12.dp).verticalScroll(
                rememberScrollState()
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ParticipantsCard(
                participants = participants,
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .aspectRatio(16f / 9f)
                    .border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
            ) {
                if (slideUrl.isNullOrBlank()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_folder),
                            contentDescription = "No slide",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No slide yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Waiting for the presenter to share.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    SubcomposeAsyncImage(
                        model = slideUrl,
                        contentDescription = "Slide",
                        modifier = Modifier.fillMaxSize().aspectRatio(16f / 9f),
                        loading = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            when (question) {
                is ViewState.Success -> {
                    val questionResponse = (question as ViewState.Success).data
                    when (questionResponse.type) {
                        "MCQ" -> {
                            MultipleChoiceQuestion(
                                questionData = questionResponse,
                                onQuestionAnswered = {
                                    inSessionViewModel.createAnswer(
                                        answerRequest = AnswerRequest(
                                            contextId = slidePageId.toString(),
                                            questionId = questionResponse.id,
                                            type = AnswerType.SUBMISSION,
                                            text = null,
                                            selectedOptionId = it,
                                            answerSource = AnswerSource(
                                                type = "slide",
                                                contextId = slidePageId.toString()
                                            )
                                        )
                                    )
                                }
                            )
                        }
                        "ESSAY" -> {
                            EssayQuestion(
                                questionData = questionResponse,
                                onQuestionAnswered = {
                                    inSessionViewModel.createAnswer(
                                        answerRequest = AnswerRequest(
                                            contextId = slidePageId.toString(),
                                            questionId = questionResponse.id,
                                            type = AnswerType.SUBMISSION,
                                            text = it,
                                            selectedOptionId = null,
                                            answerSource = AnswerSource(
                                                type = "slide",
                                                contextId = slidePageId.toString()
                                            )
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
                is ViewState.Loading -> {
                    Text("Loading")
                }
                else -> {}
            }
            when (answer) {
                is ViewState.Success -> {
                    Text(
                        text = "Answer submitted\nScore: ${(answer as ViewState.Success).data.score}",
                        textAlign = TextAlign.Center
                    )
                }
                is ViewState.Loading -> {
                    Text("Submitting answer")
                    Spacer(modifier = Modifier.height(5.dp))
                    LinearProgressIndicator()
                }
                else -> {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatBottomSheet(
    messages: List<ChatMessageSession>,
    messageText: String,
    onMessageTextChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onDismiss: () -> Unit,
    listState: LazyListState
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Chat",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom),
            ) {
                if (messages.isEmpty()) {
                    item {
                        Text(
                            text = "No messages yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                items(
                    items = messages,
                    key = { "${it.timestamp}_${it.senderName}" }
                ) { message ->
                    MessageCard(
                        message = message.message,
                        senderName = message.senderName,
                        timestamp = message.timestamp,
                        participant = null
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = messageText,
                    onValueChange = onMessageTextChange,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp)),
                    placeholder = { Text("Enter message...") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium
                )
                IconButton(
                    onClick = onSendMessage,
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun ParticipantsCard(
    participants: List<SessionParticipant>,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Card(
        modifier = modifier.animateContentSize(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Participants",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ParticipantCountChip(
                        count = participants.size,
                        compact = true
                    )
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (expanded) "Collapse participants" else "Expand participants"
                        )
                    }
                }
            }
            if (expanded) {
                if (participants.isEmpty()) {
                    Text(
                        text = "No participants yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 220.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(participants) { participant ->
                            val displayName = participantLabel(participant)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = displayName.take(1).uppercase(),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Text(
                                    text = displayName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun participantLabel(id: String): String {
    val shortId = if (id.length > 6) id.takeLast(6) else id
    return "User $shortId"
}

private fun participantLabel(participant: SessionParticipant): String {
    val fallback = participantLabel(participant.id)
    return participant.name.ifBlank { fallback }
}

@Composable
private fun ParticipantCountChip(
    count: Int,
    compact: Boolean = false
) {
    val horizontalPadding = if (compact) 8.dp else 10.dp
    val verticalPadding = if (compact) 4.dp else 6.dp
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_user),
            contentDescription = "Participants",
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(if (compact) 14.dp else 16.dp)
        )
        Text(
            text = count.toString(),
            style = if (compact) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.SemiBold
        )
    }
}
