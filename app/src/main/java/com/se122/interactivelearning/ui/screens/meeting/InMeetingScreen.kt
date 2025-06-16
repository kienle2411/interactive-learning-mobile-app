package com.se122.interactivelearning.ui.screens.meeting

import android.os.Message
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil3.compose.AsyncImage
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.placeholder
import com.eygraber.compose.placeholder.material3.shimmer
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.se122.interactivelearning.R
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.ui.components.AssignmentCard
import com.se122.interactivelearning.ui.components.MessageCard
import com.se122.interactivelearning.ui.theme.RedButton
import io.livekit.android.compose.local.RoomScope
import io.livekit.android.compose.ui.VideoTrackView
import io.livekit.android.compose.local.RoomLocal
import io.livekit.android.room.track.Track
import io.livekit.android.room.track.VideoTrack
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.json.JSONObject


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun InMeetingScreen(
    id: String,
    onEndCallClick: () -> Unit,
    inMeetingViewModel: InMeetingViewModel = hiltViewModel(),
    meetingSharedViewModel: MeetingSharedViewModel,
) {
    val coroutineScope = rememberCoroutineScope()

    val modalBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var showChatSheet by remember { mutableStateOf(false) }
    var messageText by remember { mutableStateOf("") }

    val cameraEnabled by meetingSharedViewModel.cameraState.collectAsState()
    val microEnabled by meetingSharedViewModel.microState.collectAsState()

    val meetingDetails by inMeetingViewModel.meeting.collectAsState()

    val messages by inMeetingViewModel.chatMessages.collectAsState()

    val remoteVideoTracks by inMeetingViewModel.remoteVideoTracks.collectAsState()
    val localVideoTrack by inMeetingViewModel.localVideoTrack.collectAsState()
    val remoteScreenVideo by inMeetingViewModel.remoteScreenVideo.collectAsState()

    val room by inMeetingViewModel.room.collectAsState()

    val participants by inMeetingViewModel.participants.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(id) {
        inMeetingViewModel.loadMeeting(id)
        inMeetingViewModel.joinMeeting(id)
    }

    LaunchedEffect(Unit) {
        inMeetingViewModel.uiToast.collect {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
        }
    }

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(
                drawerShape = RoundedCornerShape(0),
            ) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Text("Participants")
                    LazyColumn {
                        items(participants) {
                            Row(
                                modifier = Modifier.padding(5.dp),
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                val metadata = it?.metadata
                                if (metadata != null) {
                                    val json = JSONObject(metadata)
                                    val avatar = json.optString("avatar")
                                    AsyncImage(
                                        model = avatar ?: R.drawable.img_avatar,
                                        contentDescription = "avatar",
                                        modifier = Modifier.size(20.dp).clip(RoundedCornerShape(20.dp)).border(1.dp, Color.Gray, RoundedCornerShape(20.dp))
                                    )
                                }
                                Text(
                                    text = it?.name ?: ""
                                )
                            }
                        }
                    }
                }
            }
        },
        drawerState = drawerState,
        modifier = Modifier.fillMaxSize(),
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    windowInsets = WindowInsets(0),
                    title = {
                        Text(
                            text = if (meetingDetails is ViewState.Success) {
                                (meetingDetails as ViewState.Success).data.title
                            } else "",
                            modifier = Modifier
                                .fillMaxWidth()
                                .placeholder(
                                    visible = (meetingDetails is ViewState.Loading),
                                    highlight = PlaceholderHighlight.shimmer()
                                )
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More"
                            )
                        }
                    }
                )
            },
            bottomBar = {
                BottomAppBar(
                    windowInsets = WindowInsets(0),
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                        .background(Color.Gray),
                    containerColor = Color.Transparent
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                meetingSharedViewModel.toogleCamera()
                                inMeetingViewModel.toggleCamera(!cameraEnabled)
                            }
                        ) {
                            Icon(
                                painter = if (cameraEnabled) painterResource(R.drawable.videocam_on) else painterResource(
                                    R.drawable.videocam_off
                                ),
                                contentDescription = "Video",
                            )
                        }
                        Button(
                            onClick = {
                                meetingSharedViewModel.toogleMic()
                                inMeetingViewModel.toggleMic(!microEnabled)
                            }
                        ) {
                            Icon(
                                painter = if (microEnabled) painterResource(R.drawable.mic_on) else painterResource(R.drawable.mic_off),
                                contentDescription = "Mic",
                            )
                        }
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    showChatSheet = true
                                    modalBottomSheetState.show()
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_chat),
                                contentDescription = "Chat",
                            )
                        }
                        Button(
                            onClick = {
                                inMeetingViewModel.disconnect()
                                onEndCallClick()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RedButton
                            )
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_call_end),
                                contentDescription = "End call",
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            if (showChatSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showChatSheet = false },
                    sheetState = modalBottomSheetState,
                    containerColor = MaterialTheme.colorScheme.background,
                ) {
                    Column(
                        modifier = Modifier.padding(15.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Bottom),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(messages) {
                                MessageCard(message = it.message, timestamp = it.timestamp, participant = it.participant, senderName = it.senderName)
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            TextField(
                                value = messageText,
                                onValueChange = { messageText = it },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("Enter message...") }
                            )
                            IconButton(
                                onClick = {
                                    if (messageText.isNotBlank()) {
                                        inMeetingViewModel.sendMessage(messageText)
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
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (meetingDetails) {
                    is ViewState.Success -> {
                        room?.let {
                            CompositionLocalProvider(RoomLocal provides it) {
                                Column(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    if (remoteScreenVideo != null) {
                                        VideoTrackView(
                                            videoTrack = remoteScreenVideo!!,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(16f / 9f)
                                        )
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState())
                                    ) {
                                        participants.forEach {
                                            if (it != null && it.isCameraEnabled()) {
                                                VideoTrackView(
                                                    videoTrack = it.getTrackPublication(Track.Source.CAMERA)?.track as VideoTrack?,
                                                    modifier = Modifier
                                                        .height(200.dp)
                                                        .aspectRatio(16f / 9f)
                                                        .clip(RoundedCornerShape(20.dp))

                                                )
                                            }
                                        }
//                                        remoteVideoTracks.forEach {
//                                            if (it.enabled) {
//                                                VideoTrackView(
//                                                    videoTrack = it,
//                                                    modifier = Modifier
//                                                        .height(200.dp)
//                                                        .aspectRatio(16f / 9f)
//                                                        .clip(RoundedCornerShape(20.dp))
//                                                        .border(1.dp, Color.Gray, RoundedCornerShape(20.dp))
//                                                )
//                                            }
//                                            else {
//                                                Box(
//                                                    modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(Color.Gray).height(200.dp).aspectRatio(16f / 9f),
//                                                    contentAlignment = Alignment.Center
//                                                ) {
//                                                    Image(
//                                                        painter = painterResource(R.drawable.videocam_off),
//                                                        contentDescription = "No camera",
//                                                        modifier = Modifier.size(40.dp)
//                                                    )
//                                                }
//                                            }
//                                        }
                                    }
                                    if (!cameraEnabled) {
                                        Box(
                                            modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(Color.Gray).height(200.dp).aspectRatio(16f / 9f),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Image(
                                                painter = painterResource(R.drawable.videocam_off),
                                                contentDescription = "No camera",
                                                modifier = Modifier.size(40.dp)
                                            )
                                        }
                                    } else {
                                        localVideoTrack?.let {
                                            VideoTrackView(
                                                videoTrack = it,
                                                modifier = Modifier
                                                    .height(200.dp)
                                                    .aspectRatio(16f / 9f)
                                                    .clip(RoundedCornerShape(20.dp))
                                                    .border(1.dp, Color.Gray, RoundedCornerShape(20.dp))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}