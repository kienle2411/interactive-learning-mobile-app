package com.se122.interactivelearning.ui.screens.meeting

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.widget.GridLayout
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.se122.interactivelearning.R

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MeetingJoinScreen(
    id: String,
    onBackClick: () -> Unit,
    onJoinClick: (String) -> Unit,
    meetingJoinViewModel: MeetingJoinViewModel = hiltViewModel(),
    meetingSharedViewModel: MeetingSharedViewModel
) {
    val lifeCycleOwner = LocalLifecycleOwner.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val microPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    val cameraEnabled by meetingSharedViewModel.cameraState.collectAsState()
    val microEnabled by meetingSharedViewModel.microState.collectAsState()

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted && cameraEnabled) {
            cameraPermissionState.launchPermissionRequest()
        }
        if (!microPermissionState.status.isGranted && microEnabled) {
            microPermissionState.launchPermissionRequest()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row {
            IconButton(
                onClick = {
                    onBackClick()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
        Box(
            modifier = Modifier.height(screenHeight / 2).border(1.dp, Color.Gray, RoundedCornerShape(10.dp)).clip(RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.BottomCenter
        ) {
            if (cameraPermissionState.status.isGranted && cameraEnabled) {
                AndroidView(
                    modifier = Modifier.fillMaxWidth(),
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.surfaceProvider = previewView.surfaceProvider
                            }
                            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifeCycleOwner,
                                    cameraSelector,
                                    preview
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }, ContextCompat.getMainExecutor(ctx))
                        previewView
                    },
                )
            } else {
                if (!cameraPermissionState.status.isGranted) {
                    Text(
                        text = "Please grant camera permission to continue.",
                        modifier = Modifier.fillMaxSize(),
                        textAlign = TextAlign.Center
                    )
                }
            }
            if (!microPermissionState.status.isGranted && microEnabled) {
                Text(
                    text = "Please grant microphone permission to continue.",
                    modifier = Modifier.fillMaxSize(),
                    textAlign = TextAlign.Center
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
            ) {
                Button(
                    onClick = {
                        meetingSharedViewModel.toogleCamera()
                    }
                ) {
                    Icon(
                        painter = if (cameraEnabled) painterResource(R.drawable.videocam_on) else painterResource(R.drawable.videocam_off),
                        contentDescription = "Camera",
                    )
                }
                Button(
                    onClick = {
                        meetingSharedViewModel.toogleMic()
                    }
                ) {
                    Icon(
                        painter = if (microEnabled) painterResource(R.drawable.mic_on) else painterResource(R.drawable.mic_off),
                        contentDescription = "Micro",
                    )
                }
            }
        }
        Button(
            onClick = {
                onJoinClick(id)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Join")
        }
    }
}