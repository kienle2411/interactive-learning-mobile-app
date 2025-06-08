package com.se122.interactivelearning.ui.screens.meeting

import android.Manifest
import android.provider.CalendarContract
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
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
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.placeholder
import com.eygraber.compose.placeholder.material3.shimmer
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.se122.interactivelearning.R
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.ui.theme.RedButton
import io.livekit.android.compose.local.RoomScope
import io.livekit.android.compose.ui.VideoTrackView
import io.livekit.android.compose.local.RoomLocal


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun InMeetingScreen(
    id: String,
    onEnđCallClick: () -> Unit,
    inMeetingViewModel: InMeetingViewModel = hiltViewModel(),
    meetingSharedViewModel: MeetingSharedViewModel,
) {
    val lifeCycleOwner = LocalLifecycleOwner.current

    val cameraEnabled by meetingSharedViewModel.cameraState.collectAsState()
    val microEnabled by meetingSharedViewModel.microState.collectAsState()

    val meetingDetails by inMeetingViewModel.meeting.collectAsState()

    val remoteVideoTracks by inMeetingViewModel.remoteVideoTracks.collectAsState()
    val localVideoTrack by inMeetingViewModel.localVideoTrack.collectAsState()

    val room by inMeetingViewModel.room.collectAsState()

    LaunchedEffect(id) {
        inMeetingViewModel.loadMeeting(id)
        inMeetingViewModel.joinMeeting(id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
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
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Color.Gray),
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
                            painter = if (cameraEnabled) painterResource(R.drawable.videocam_on) else painterResource(R.drawable.videocam_off),
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
                        onClick = {}
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_chat),
                            contentDescription = "Chat",
                        )
                    }
                    Button(
                        onClick = {},
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
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (meetingDetails) {
                is ViewState.Success -> {
                    room?.let {
                        CompositionLocalProvider(RoomLocal provides it) {
                            Column(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                remoteVideoTracks.forEach { track ->
                                    Text("Outside")
                                    VideoTrackView(
                                        videoTrack = track,
                                        modifier = Modifier
                                            .padding(16.dp)
                                    )
                                }
//                                localVideoTrack?.let {
//                                    Text("Local")
//                                    VideoTrackView(
//                                        videoTrack = it,
//                                        modifier = Modifier
//                                            .fillMaxSize()
//                                            .padding(16.dp)
//                                    )
//                                }
                            }
                        }
                    }
                }
                else -> {}
            }
            if (cameraEnabled) {

//                AndroidView(
//                    modifier = Modifier.fillMaxWidth(),
//                    factory = { ctx ->
//                        val previewView = PreviewView(ctx)
//                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
//
//                        cameraProviderFuture.addListener({
//                            val cameraProvider = cameraProviderFuture.get()
//                            val preview = Preview.Builder().build().also {
//                                it.surfaceProvider = previewView.surfaceProvider
//                            }
//                            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
//
//                            try {
//                                cameraProvider.unbindAll()
//                                cameraProvider.bindToLifecycle(
//                                    lifeCycleOwner,
//                                    cameraSelector,
//                                    preview
//                                )
//                            } catch (e: Exception) {
//                                e.printStackTrace()
//                            }
//                        }, ContextCompat.getMainExecutor(ctx))
//                        previewView
//                    },
//                )
            }

        }
    }
}