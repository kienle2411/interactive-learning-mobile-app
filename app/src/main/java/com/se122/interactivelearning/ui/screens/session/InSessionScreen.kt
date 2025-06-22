package com.se122.interactivelearning.ui.screens.session

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.se122.interactivelearning.R
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.ui.components.MessageCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InSessionScreen(
    sessionId: String,
    onLeaveClick: () -> Unit,
    inSessionViewModel: InSessionViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    val session by inSessionViewModel.session.collectAsState()

    val slideUrl by inSessionViewModel.slideUrl.collectAsState()

    val messages by inSessionViewModel.messages.collectAsState()

    val modalBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    var showChatSheet by remember { mutableStateOf(false) }
    var messageText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        inSessionViewModel.getSession(sessionId)
        inSessionViewModel.connectSocket()
        inSessionViewModel.joinSession(sessionId)
    }

    DisposableEffect(Unit) {
        onDispose {
            inSessionViewModel.leaveSession(sessionId)
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (session is ViewState.Success) (session as ViewState.Success).data.title else "",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                showChatSheet = true
                                modalBottomSheetState.show()
                            }
                        },
                        shape = RoundedCornerShape(5.dp)
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
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Leave Session"
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        if (showChatSheet) {
            ModalBottomSheet(
                sheetState = modalBottomSheetState,
                onDismissRequest = {
                    showChatSheet = false
                },
                containerColor = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Chat",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Bottom)
                    ) {
                        items(
                            items = messages,
                            key = { it.timestamp }
                        ) {
                            MessageCard(
                                message = it.message,
                                senderName = it.senderName,
                                timestamp = it.timestamp,
                                participant = null
                            )
                        }
                    }
                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Enter message...") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    if (messageText.isNotBlank()) {
                                        inSessionViewModel.sendMessage(sessionId, messageText)
                                        messageText = ""
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send"
                                )
                            }
                        }
                    )
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .aspectRatio(16f / 9f)
                    .border(1.dp, Color.Gray, RoundedCornerShape(5.dp))
            ) {
                SubcomposeAsyncImage(
                    model = slideUrl ?: "",
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
    }
}