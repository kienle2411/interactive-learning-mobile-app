package com.se122.interactivelearning.ui.screens.meeting

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.internal.GsonBuildConfig
import com.se122.interactivelearning.BuildConfig
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.MeetingAccessTokenResponse
import com.se122.interactivelearning.data.remote.dto.MeetingResponse
import com.se122.interactivelearning.domain.usecase.meeting.GetMeetingAccessTokenUseCase
import com.se122.interactivelearning.domain.usecase.meeting.GetMeetingInformationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.livekit.android.ConnectOptions
import io.livekit.android.LiveKit
import io.livekit.android.RoomOptions
import io.livekit.android.compose.local.RoomScope
import io.livekit.android.events.EventListenable
import io.livekit.android.events.RoomEvent
import io.livekit.android.events.collect
import io.livekit.android.room.Room
import io.livekit.android.room.track.LocalVideoTrackOptions
import io.livekit.android.room.track.VideoTrack
import io.socket.client.IO
import io.socket.client.IO.socket
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InMeetingViewModel @Inject constructor(
    private val getMeetingInformationUseCase: GetMeetingInformationUseCase,
    private val getMeetingAccessTokenUseCase: GetMeetingAccessTokenUseCase,
    @ApplicationContext private val context: Context
): ViewModel() {
    private val _meeting = MutableStateFlow<ViewState<MeetingResponse>>(ViewState.Idle)
    val meeting = _meeting.asStateFlow()

    private val _room = MutableStateFlow<Room?>(null)
    val room = _room.asStateFlow()

    private val _remoteVideoTracks = MutableStateFlow<List<VideoTrack>>(emptyList())
    val remoteVideoTracks = _remoteVideoTracks.asStateFlow()

    private val _localVideoTrack = MutableStateFlow<VideoTrack?>(null)
    val localVideoTrack = _localVideoTrack.asStateFlow()

    fun loadMeeting(id: String) {
        viewModelScope.launch {
            _meeting.value = ViewState.Loading
            when (val result = getMeetingInformationUseCase(id)) {
                is ApiResult.Success -> {
                    _meeting.value = ViewState.Success(result.data)
                }
                is ApiResult.Error -> {
                    val msg = (result.message + " " + result.errors?.first())
                    _meeting.value = ViewState.Error(msg)
                    }
                is ApiResult.Exception -> {
                    val msg = "Unknown error"
                    _meeting.value = ViewState.Error(msg)
                }
            }
        }
    }

    fun joinMeeting(meetingId: String) {
        viewModelScope.launch {
            when (val result = getMeetingAccessTokenUseCase(meetingId)) {
                is ApiResult.Success -> {
                    connectToLiveKitRoom(result.data.accessToken)
                }
                else -> {}
            }
        }
    }

    private fun connectToLiveKitRoom(token: String) {
        viewModelScope.launch {
            val liveKitUrl = BuildConfig.LIVEKIT_URL
            try {
                Log.i("Meeting", "Connect")
                val room = LiveKit.create(
                    appContext = context,
                    options = RoomOptions(
                        adaptiveStream = true,
                        dynacast = true,

                    )
                )
                _room.value = room
                room.connect(liveKitUrl, token, ConnectOptions(autoSubscribe = true))

                val videoTrack = room.localParticipant.createVideoTrack(
                    name = "Camera",
                    options = LocalVideoTrackOptions()
                )
                videoTrack.let {
                    room.localParticipant.publishVideoTrack(it)
                }

                _localVideoTrack.value = videoTrack

                val audioTrack = room.localParticipant.createAudioTrack(
                    name = "Microphone"
                )

                audioTrack.let {
                    room.localParticipant.publishAudioTrack(it)
                }

                room.localParticipant.setCameraEnabled(true)
                room.localParticipant.setMicrophoneEnabled(true)

                observeRoomEvents()
            } catch (e: Exception) {
                Log.i("Meeting", "Exception$e")
            }
        }
    }

    private fun observeRoomEvents() {
        viewModelScope.launch {
           _room.value?.events?.collect { event ->
               when (event) {
                   is RoomEvent.ParticipantConnected -> {
                       Log.i("Meeting", "Connected")
                   }
                   is RoomEvent.ParticipantDisconnected -> {

                   }
                   is RoomEvent.Disconnected -> {
                       _remoteVideoTracks.value = emptyList()
                       _localVideoTrack.value = null
                   }
                   is RoomEvent.TrackSubscribed -> {
                        if (event.track is VideoTrack) {
                            _remoteVideoTracks.value = _remoteVideoTracks.value + (event.track as VideoTrack)
                        }
                       Log.i("Meeting", "Subscribed")
                   }
                   else -> {
                       Log.i("Meeting", "Unknown event")
                   }
               }
           }
        }
    }

    fun toggleCamera(isEnabled: Boolean) {
        viewModelScope.launch {
            _room.value?.localParticipant?.setCameraEnabled(isEnabled)
            Log.i("Camera", isEnabled.toString())
        }
    }

    fun toggleMic(isEnabled: Boolean) {
        viewModelScope.launch {
            _room.value?.localParticipant?.setMicrophoneEnabled(isEnabled)
            Log.i("Mic", isEnabled.toString())
        }
    }

    override fun onCleared() {
        super.onCleared()
        _room.value?.disconnect()
        _room.value = null
    }
}