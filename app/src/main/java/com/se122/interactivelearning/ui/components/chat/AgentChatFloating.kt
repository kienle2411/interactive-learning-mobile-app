package com.se122.interactivelearning.ui.components.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun AgentChatFloating(
    isOpen: Boolean,
    title: String,
    messages: List<ChatUiMessage>,
    isSending: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onToggle: () -> Unit,
    onSend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val maxWidth = (configuration.screenWidthDp * 0.92f).dp
    val listState = rememberLazyListState()
    var inputValue by remember { mutableStateOf("") }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(modifier = modifier) {
        AnimatedVisibility(visible = isOpen) {
            Card(
                modifier = Modifier
                    .widthIn(min = 280.dp, max = maxWidth)
                    .wrapContentHeight()
                    .imePadding(),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (isLoading) {
                                Text(
                                    text = "Dang tai ket noi...",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        IconButton(onClick = onToggle) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close chat"
                            )
                        }
                    }

                    if (!errorMessage.isNullOrBlank()) {
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(messages, key = { it.id }) { message ->
                            ChatBubble(message = message)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = inputValue,
                            onValueChange = { inputValue = it },
                            modifier = Modifier.weight(1f),
                            placeholder = {
                                Text(text = "Hỏi trợ lý...")
                            },
                            maxLines = 4,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FloatingActionButton(
                            onClick = {
                                val text = inputValue.trim()
                                if (text.isNotEmpty()) {
                                    onSend(text)
                                    inputValue = ""
                                }
                            },
                            modifier = Modifier.size(40.dp),
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Send,
                                contentDescription = "Send"
                            )
                        }
                    }
                    if (isSending) {
                        Text(
                            text = "Dang gui...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onToggle,
            modifier = Modifier.align(Alignment.BottomEnd),
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = if (isOpen) Icons.Filled.Close else Icons.Filled.ChatBubbleOutline,
                contentDescription = "Toggle chat"
            )
        }
    }
}

@Composable
private fun ChatBubble(message: ChatUiMessage) {
    val isUser = message.role == ChatRole.USER
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isUser) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = if (isUser) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}
