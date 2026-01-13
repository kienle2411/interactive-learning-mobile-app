package com.se122.interactivelearning.ui.screens.meeting

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.se122.interactivelearning.BuildConfig
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.MeetingAccessTokenResponse
import com.se122.interactivelearning.data.remote.dto.MeetingResponse
import com.se122.interactivelearning.data.remote.dto.ProfileResponse
import com.se122.interactivelearning.domain.model.ChatMessage
import com.se122.interactivelearning.domain.model.ChatPayload
import com.se122.interactivelearning.domain.usecase.meeting.GetMeetingAccessTokenUseCase
import com.se122.interactivelearning.domain.usecase.meeting.GetMeetingInformationUseCase
import com.se122.interactivelearning.domain.usecase.profile.GetProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.livekit.android.ConnectOptions
import io.livekit.android.LiveKit
import io.livekit.android.RoomOptions
import io.livekit.android.events.RoomEvent
import io.livekit.android.events.collect
import io.livekit.android.room.Room
import io.livekit.android.room.participant.Participant
import io.livekit.android.room.track.DataPublishReliability
import io.livekit.android.room.track.LocalVideoTrackOptions
import io.livekit.android.room.track.VideoTrack
import io.livekit.android.room.track.Track.Source
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InMeetingViewModel @Inject constructor(
    private val getMeetingInformationUseCase: GetMeetingInformationUseCase,
    private val getMeetingAccessTokenUseCase: GetMeetingAccessTokenUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // State flows
    private val _meeting = MutableStateFlow<ViewState<MeetingResponse>>(ViewState.Idle)
    val meeting = _meeting.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages = _chatMessages.asStateFlow()

    private val _uiToast = MutableSharedFlow<String>()
    val uiToast = _uiToast.asSharedFlow()

    private val _room = MutableStateFlow<Room?>(null)
    val room = _room.asStateFlow()

    private val _remoteVideoTracks = MutableStateFlow<List<VideoTrack>>(emptyList())
    val remoteVideoTracks = _remoteVideoTracks.asStateFlow()

    private val _remoteScreenVideo = MutableStateFlow<VideoTrack?>(null)
    val remoteScreenVideo = _remoteScreenVideo.asStateFlow()

    private val _localVideoTrack = MutableStateFlow<VideoTrack?>(null)
    val localVideoTrack = _localVideoTrack.asStateFlow()

    private val _profile = MutableStateFlow<ProfileResponse?>(null)
    val profile = _profile.asStateFlow()

    private val _participants = MutableStateFlow<List<Participant>>(emptyList())
    val participants = _participants.asStateFlow()

    init {
        initialize()
    }

    // region Initialization
    private fun initialize() {
        viewModelScope.launch {
            _uiToast.emit("Connecting...")
            loadProfile()
            updateParticipantList()
        }
    }

    private suspend fun loadProfile() {
        when (val result = getProfileUseCase.invoke()) {
            is ApiResult.Success -> _profile.update { result.data }
            else -> Log.e("Meeting", "Failed to load profile")
        }
    }
    // endregion

    // region Meeting Management
    fun loadMeeting(id: String) {
        viewModelScope.launch {
            _meeting.value = ViewState.Loading
            when (val result = getMeetingInformationUseCase(id)) {
                is ApiResult.Success -> _meeting.update { ViewState.Success(result.data) }
                is ApiResult.Error -> _meeting.update { ViewState.Error("Error: ${result.message}") }
                is ApiResult.Exception -> _meeting.update { ViewState.Error("Unknown error") }
            }
        }
    }

    fun joinMeeting(meetingId: String) {
        viewModelScope.launch {
            when (val result = getMeetingAccessTokenUseCase(meetingId)) {
                is ApiResult.Success -> connectToLiveKitRoom(result.data.accessToken)
                else -> _uiToast.emit("Failed to join meeting")
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            _room.value?.disconnect()
            clearRoomState()
            _uiToast.emit("Left meeting")
        }
    }

    private fun clearRoomState() {
        _room.update { null }
        _remoteVideoTracks.update { emptyList() }
        _localVideoTrack.update { null }
        _remoteScreenVideo.update { null }
        _participants.update { emptyList() }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
    // endregion

    // region Media Control
    fun toggleCamera(isEnabled: Boolean) {
        viewModelScope.launch {
            _room.value?.localParticipant?.setCameraEnabled(isEnabled)
                ?: _uiToast.emit("Room not initialized")
        }
    }

    fun toggleMic(isEnabled: Boolean) {
        viewModelScope.launch {
            _room.value?.localParticipant?.setMicrophoneEnabled(isEnabled)
                ?: _uiToast.emit("Room not initialized")
        }
    }
    // endregion

    // region Chat Management
    fun sendMessage(message: String) {
        viewModelScope.launch {
            val room = _room.value ?: run {
                _uiToast.emit("Room not initialized")
                return@launch
            }
            val participant = room.localParticipant ?: run {
                _uiToast.emit("Participant not initialized")
                return@launch
            }
            val payload = ChatPayload(
                senderId = _profile.value?.id.toString(),
                senderName = "${_profile.value?.firstName} ${_profile.value?.lastName}",
                message = message
            )
            try {
                val json = Gson().toJson(payload)
                participant.publishData(
                    data = json.toByteArray(Charsets.UTF_8),
                    reliability = DataPublishReliability.RELIABLE
                )
                _chatMessages.update { it + ChatMessage(
                    message = message,
                    participant = participant,
                    timestamp = System.currentTimeMillis(),
                    senderName = participant.name ?: ""
                ) }
                Log.i("Meeting", "Sent message: $message")
            } catch (e: Exception) {
                Log.e("Meeting", "Failed to send message", e)
                _uiToast.emit("Failed to send message")
            }
        }
    }
    // endregion

    // region Participant Management
    private fun updateParticipantList() {
        viewModelScope.launch {
            val room = _room.value ?: return@launch
            val participants = mutableListOf<Participant>()
            room.localParticipant?.let { participants.add(it) }
            room.remoteParticipants.values.forEach { participants.add(it) }
            _participants.update { participants }
        }
    }
    // endregion

    // region LiveKit Room
    private fun connectToLiveKitRoom(token: String) {
        viewModelScope.launch {
            try {
                val room = LiveKit.create(
                    appContext = context,
                    options = RoomOptions(adaptiveStream = true, dynacast = true)
                )
                _room.update { room }
                room.connect(BuildConfig.LIVEKIT_URL, token, ConnectOptions(autoSubscribe = true))

                setupLocalTracks(room)
                setupRemoteTracks(room)
                room.localParticipant?.setCameraEnabled(true)
                room.localParticipant?.setMicrophoneEnabled(true)

                observeRoomEvents()
                updateParticipantList()
            } catch (e: Exception) {
                Log.e("Meeting", "Connection failed", e)
                _uiToast.emit("Failed to connect to room")
            }
        }
    }

    private suspend fun setupLocalTracks(room: Room) {
        val videoTrack = room.localParticipant.createVideoTrack(
            name = "Camera",
            options = LocalVideoTrackOptions()
        )
        room.localParticipant.publishVideoTrack(videoTrack)
        _localVideoTrack.update { videoTrack }

        val audioTrack = room.localParticipant.createAudioTrack(name = "Microphone")
        room.localParticipant.publishAudioTrack(audioTrack)
    }

    private fun setupRemoteTracks(room: Room) {
        room.remoteParticipants.values.forEach { participant ->
            participant.trackPublications.values.forEach { publication ->
                if (publication.kind == io.livekit.android.room.track.Track.Kind.VIDEO) {
                    publication.track?.let { track ->
                        if (publication.source == Source.SCREEN_SHARE) {
                            _remoteScreenVideo.update { track as VideoTrack }
                        } else {
                            _remoteVideoTracks.update { it + (track as VideoTrack) }
                        }
                    }
                }
            }
        }
    }

    private fun observeRoomEvents() {
        viewModelScope.launch {
            _room.value?.events?.collect { event ->
                when (event) {
                    is RoomEvent.ParticipantConnected -> {
                        _uiToast.emit("New participant connected")
                        updateParticipantList()
                    }
                    is RoomEvent.ParticipantDisconnected -> {
                        _uiToast.emit("Participant disconnected")
                        updateParticipantList()
                    }
                    is RoomEvent.Disconnected -> {
                        clearRoomState()
                        _uiToast.emit("Disconnected")
                    }
                    is RoomEvent.Reconnecting -> _uiToast.emit("Reconnecting...")
                    is RoomEvent.TrackSubscribed -> {
                        if (event.track is VideoTrack) {
                            if (event.publication.source == Source.SCREEN_SHARE) {
                                _remoteScreenVideo.update { event.track as VideoTrack }
                            } else {
                                _remoteVideoTracks.update { it + (event.track as VideoTrack) }
                            }
                        }
                        Log.i("Meeting", "Track subscribed")
                    }
                    is RoomEvent.DataReceived -> handleDataReceived(event)
                    else -> {}
                }
            }
        }
    }

    private suspend fun handleDataReceived(event: RoomEvent.DataReceived) {
        val messageJson = event.data.toString(Charsets.UTF_8)
        try {
            val payload = Gson().fromJson(messageJson, ChatPayload::class.java)
            if (payload.message.isNotEmpty()) {
                _chatMessages.update { it + ChatMessage(
                    message = payload.message,
                    participant = event.participant,
                    timestamp = System.currentTimeMillis(),
                    senderName = payload.senderName
                ) }
                _uiToast.emit("${payload.senderName}: ${payload.message}")
            }
        } catch (e: Exception) {
            Log.e("Meeting", "Failed to parse message: $messageJson", e)
            _uiToast.emit("Failed to parse message")
        }
    }
    // endregion
}