package com.se122.interactivelearning.ui.screens.meeting

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.placeholder
import com.eygraber.compose.placeholder.material3.shimmer
import com.se122.interactivelearning.R
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.data.remote.dto.MeetingResponse
import com.se122.interactivelearning.domain.model.ChatMessage
import com.se122.interactivelearning.ui.components.MessageCard
import com.se122.interactivelearning.ui.theme.RedButton
import io.livekit.android.compose.local.RoomLocal
import io.livekit.android.compose.ui.VideoTrackView
import io.livekit.android.room.Room
import io.livekit.android.room.participant.Participant
import io.livekit.android.room.track.Track
import io.livekit.android.room.track.VideoTrack
import kotlinx.coroutines.launch
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InMeetingScreen(
    id: String,
    onEndCallClick: () -> Unit,
    inMeetingViewModel: InMeetingViewModel = hiltViewModel(),
    meetingSharedViewModel: MeetingSharedViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    var showChatSheet by remember { mutableStateOf(false) }
    var messageText by remember { mutableStateOf("") }

    val cameraEnabled by meetingSharedViewModel.cameraState.collectAsState()
    val microEnabled by meetingSharedViewModel.microState.collectAsState()
    val meetingDetails by inMeetingViewModel.meeting.collectAsState()
    val messages by inMeetingViewModel.chatMessages.collectAsState()
    val room by inMeetingViewModel.room.collectAsState()
    val participants by inMeetingViewModel.participants.collectAsState()
    val remoteVideoTracks by inMeetingViewModel.remoteVideoTracks.collectAsState()
    val localVideoTrack by inMeetingViewModel.localVideoTrack.collectAsState()
    val remoteScreenVideo by inMeetingViewModel.remoteScreenVideo.collectAsState()

    // Auto-scroll to the latest message
    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Load meeting and join
    LaunchedEffect(id) {
        inMeetingViewModel.loadMeeting(id)
        inMeetingViewModel.joinMeeting(id)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(participants)
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                MeetingTopBar(meetingDetails, onParticipantsClick = {
                    coroutineScope.launch { drawerState.open() }
                })
            },
            bottomBar = {
                MeetingBottomBar(
                    cameraEnabled = cameraEnabled,
                    microEnabled = microEnabled,
                    onCameraToggle = {
                        meetingSharedViewModel.toogleCamera()
                        inMeetingViewModel.toggleCamera(!cameraEnabled)
                    },
                    onMicToggle = {
                        meetingSharedViewModel.toogleMic()
                        inMeetingViewModel.toggleMic(!microEnabled)
                    },
                    onChatClick = { showChatSheet = true },
                    onEndCallClick = {
                        inMeetingViewModel.disconnect()
                        onEndCallClick()
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            MeetingContent(
                meetingDetails = meetingDetails,
                room = room,
                remoteScreenVideo = remoteScreenVideo,
                participants = participants,
                localVideoTrack = localVideoTrack,
                cameraEnabled = cameraEnabled,
                modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp, vertical = 8.dp)
            )
            if (showChatSheet) {
                ChatBottomSheet(
                    messages = messages,
                    messageText = messageText,
                    onMessageTextChange = { messageText = it },
                    onSendMessage = {
                        if (messageText.isNotBlank()) {
                            inMeetingViewModel.sendMessage(messageText)
                            messageText = ""
                        }
                    },
                    onDismiss = { showChatSheet = false },
                    listState = listState
                )
            }
        }
    }
}

@Composable
private fun DrawerContent(participants: List<Participant>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Participants",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(10.dp)
        ) {
            items(participants) { participant ->
                participant?.let {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                            .shadow(4.dp, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        val avatar = try {
                            if (!it.metadata.isNullOrBlank()) {
                                JSONObject(it.metadata).optString("avatar")
                            } else {
                                null
                            }
                        } catch (e: Exception) {
                            null
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AsyncImage(
                                model = avatar ?: R.drawable.img_avatar,
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                            )
                            Text(
                                text = it.name ?: "Unknown",
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MeetingTopBar(
    meetingDetails: ViewState<MeetingResponse>,
    onParticipantsClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        title = {
            Text(
                text = when (meetingDetails) {
                    is ViewState.Success -> meetingDetails.data.title
                    else -> ""
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .placeholder(
                        visible = meetingDetails is ViewState.Loading,
                        highlight = PlaceholderHighlight.shimmer()
                    )
            )
        },
        actions = {
            IconButton(onClick = onParticipantsClick) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Participants",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun MeetingBottomBar(
    cameraEnabled: Boolean,
    microEnabled: Boolean,
    onCameraToggle: () -> Unit,
    onMicToggle: () -> Unit,
    onChatClick: () -> Unit,
    onEndCallClick: () -> Unit
) {
    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        containerColor = Color.Transparent,
        windowInsets = WindowInsets(0)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton(
                icon = if (cameraEnabled) R.drawable.videocam_on else R.drawable.videocam_off,
                contentDescription = "Video",
                onClick = onCameraToggle
            )
            ActionButton(
                icon = if (microEnabled) R.drawable.mic_on else R.drawable.mic_off,
                contentDescription = "Mic",
                onClick = onMicToggle
            )
            ActionButton(
                icon = R.drawable.ic_chat,
                contentDescription = "Chat",
                onClick = onChatClick
            )
            ActionButton(
                icon = R.drawable.ic_call_end,
                contentDescription = "End call",
                onClick = onEndCallClick,
                containerColor = RedButton
            )
        }
    }
}

@Composable
private fun ActionButton(
    icon: Int,
    contentDescription: String,
    onClick: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.primary
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(70.dp)
            .shadow(4.dp, CircleShape),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            modifier = Modifier.size(70.dp)
        )
    }
}

@Composable
private fun MeetingContent(
    meetingDetails: ViewState<MeetingResponse>,
    room: Room?,
    remoteScreenVideo: VideoTrack?,
    participants: List<Participant>,
    localVideoTrack: VideoTrack?,
    cameraEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    when (meetingDetails) {
        is ViewState.Success -> {
            room?.let {
                CompositionLocalProvider(RoomLocal provides it) {
                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .animateContentSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        remoteScreenVideo?.let { videoTrack ->
                            VideoTrackCard(
                                videoTrack = videoTrack,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 9f)
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val localIdentity = room.localParticipant?.identity
                            participants.forEach { participant ->
                                if (participant.isCameraEnabled() && participant.identity != localIdentity) {
                                    VideoTrackCard(
                                        videoTrack = participant.getTrackPublication(Track.Source.CAMERA)?.track as VideoTrack?,
                                        modifier = Modifier
                                            .height(180.dp)
                                            .aspectRatio(16f / 9f),
                                        participantName = participant.name
                                    )
                                }
                            }
                        }
                        if (!cameraEnabled) {
                            PlaceholderVideoCard(
                                modifier = Modifier
                                    .height(180.dp)
                                    .aspectRatio(16f / 9f)
                            )
                        } else {
                            localVideoTrack?.let { videoTrack ->
                                VideoTrackCard(
                                    videoTrack = videoTrack,
                                    modifier = Modifier
                                        .height(180.dp)
                                        .aspectRatio(16f / 9f),
                                    participantName = room.localParticipant?.name
                                )
                            }
                        }
                    }
                }
            }
        }
        else -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Loading meeting...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun VideoTrackCard(
    videoTrack: VideoTrack?,
    modifier: Modifier = Modifier,
    participantName: String? = null
) {
    Card(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box {
            videoTrack?.let {
                VideoTrackView(
                    videoTrack = it,
                    modifier = Modifier.fillMaxSize()
                )
            }
            participantName?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .background(
                            Color.Black.copy(alpha = 0.6f),
                            RoundedCornerShape(topEnd = 8.dp, bottomStart = 8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun PlaceholderVideoCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.videocam_off),
                contentDescription = "No camera",
                modifier = Modifier.size(48.dp),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatBottomSheet(
    messages: List<ChatMessage>,
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
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                items(
                    items = messages,
                    key = { "${it.timestamp}_${it.senderName}" }
                ) { message ->
                    MessageCard(
                        message = message.message,
                        timestamp = message.timestamp,
                        participant = message.participant,
                        senderName = message.senderName,
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