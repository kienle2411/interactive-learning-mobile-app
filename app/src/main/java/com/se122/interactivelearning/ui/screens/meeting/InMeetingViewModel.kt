package com.se122.interactivelearning.ui.screens.meeting

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.internal.GsonBuildConfig
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
import io.livekit.android.compose.local.RoomScope
import io.livekit.android.events.EventListenable
import io.livekit.android.events.RoomEvent
import io.livekit.android.events.collect
import io.livekit.android.room.Room
import io.livekit.android.room.participant.LocalParticipant
import io.livekit.android.room.participant.Participant
import io.livekit.android.room.track.DataPublishReliability
import io.livekit.android.room.track.LocalVideoTrackOptions
import io.livekit.android.room.track.Track
import io.livekit.android.room.track.VideoTrack
import io.socket.client.IO
import io.socket.client.IO.socket
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import livekit.LivekitModels
import javax.inject.Inject
import io.livekit.android.room.track.Track.Source
import kotlinx.coroutines.flow.update
import org.json.JSONObject

@HiltViewModel
class InMeetingViewModel @Inject constructor(
    private val getMeetingInformationUseCase: GetMeetingInformationUseCase,
    private val getMeetingAccessTokenUseCase: GetMeetingAccessTokenUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    @ApplicationContext private val context: Context
): ViewModel() {
    private val _meeting = MutableStateFlow<ViewState<MeetingResponse>>(ViewState.Idle)
    val meeting = _meeting.asStateFlow()

    private var _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages = _chatMessages.asStateFlow()

    private val _uiToast = MutableSharedFlow<String>()
    val uiToast = _uiToast.asSharedFlow()

    private var _room = MutableStateFlow<Room?>(null)
    val room = _room.asStateFlow()

    private val _remoteVideoTracks = MutableStateFlow<List<VideoTrack>>(emptyList())
    val remoteVideoTracks = _remoteVideoTracks.asStateFlow()

    private val _remoteScreenVideo = MutableStateFlow<VideoTrack?>(null)
    val remoteScreenVideo = _remoteScreenVideo.asStateFlow()

    private val _localVideoTrack = MutableStateFlow<VideoTrack?>(null)
    val localVideoTrack = _localVideoTrack.asStateFlow()

    private val _profile = MutableStateFlow<ProfileResponse?>(null)
    val profile = _profile.asStateFlow()

    private val _participants = MutableStateFlow<List<Participant?>>(emptyList())
    val participants = _participants.asStateFlow()

    init {
        viewModelScope.launch {
            _uiToast.emit("Connecting...")
            getParticipantList()
            when (val result = getProfileUseCase.invoke()) {
                is ApiResult.Success -> {
                    _profile.value = result.data
                }
                else -> {}
            }
        }
    }

    fun loadMeeting(id: String) {
        viewModelScope.launch {
            _meeting.value = ViewState.Loading
            when (val result = getMeetingInformationUseCase(id)) {
                is ApiResult.Success -> {
                    _meeting.value = ViewState.Success(result.data)
                }
                is ApiResult.Error -> {
//                    val msg = (result.message + " " + result.errors?.first())
//                    _meeting.value = ViewState.Error(msg)
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

    fun sendMessage(message: String) {
        viewModelScope.launch {
            val payload = ChatPayload(
                senderId = _profile.value?.id.toString(),
                senderName = _profile.value?.firstName + " " + _profile.value?.lastName,
                message = message
            )
            val json = Gson().toJson(payload)
            _room.value?.localParticipant?.publishData(
                data = json.toByteArray(Charsets.UTF_8)
            )
            _chatMessages.value = _chatMessages.value + ChatMessage(
                message = message,
                participant = _room.value?.localParticipant,
                timestamp = System.currentTimeMillis(),
                senderName = _room.value?.localParticipant?.name ?: ""
            )
            Log.i("Meeting", _chatMessages.value.toString())
        }
    }


    fun getParticipantList() {
        viewModelScope.launch {
            _participants.value = emptyList<Participant?>()
            _participants.value = _participants.value + _room.value?.localParticipant
            _room.value?.remoteParticipants?.values?.forEach {
                _participants.value = _participants.value + it
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

                room.remoteParticipants.values.forEach { participant ->
                    participant.trackPublications.values.forEach { publication ->
                        if (publication.kind == Track.Kind.VIDEO) {
                            publication.track?.let {
                                _remoteVideoTracks.value = _remoteVideoTracks.value + (it as VideoTrack)
                            }
                        }
                        if (publication.source == Source.SCREEN_SHARE) {
                            Log.i("Meeting", "Screen share")
                            _remoteScreenVideo.value = (publication.track as VideoTrack)
                        }
                    }
                }

                room.localParticipant.setCameraEnabled(true)
                room.localParticipant.setMicrophoneEnabled(true)

                observeRoomEvents()
                getParticipantList()
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
                       _uiToast.emit("New connect")
                       getParticipantList()
                   }
                   is RoomEvent.ParticipantDisconnected -> {
                       _uiToast.emit("Participant disconnected")
                       getParticipantList()
                   }
                   is RoomEvent.Disconnected -> {
                       _remoteVideoTracks.value = emptyList()
                       _localVideoTrack.value = null
                       _remoteScreenVideo.value = null
                       _uiToast.emit("Disconnected")
                   }
                   is RoomEvent.Reconnecting -> {
                       _uiToast.emit("Reconnecting...")
                   }
//                   is RoomEvent.TrackSubscribed -> {
//                        if (event.track is VideoTrack) {
//                            _remoteVideoTracks.value = _remoteVideoTracks.value + (event.track as VideoTrack)
//                        }
//                       Log.i("Meeting", "Subscribed")
//                   }
                   is RoomEvent.TrackSubscribed -> {
                       if (event.track is VideoTrack) {
                           if (event.publication.source == Source.SCREEN_SHARE) {
                               _remoteScreenVideo.value = event.track as VideoTrack
                           } else {
                               _remoteVideoTracks.value = _remoteVideoTracks.value + (event.track as VideoTrack)
                           }
                       }
                       Log.i("Meeting", "Subscribed")
                   }
                   is RoomEvent.DataReceived -> {
                       val msgString = event.data.toString(Charsets.UTF_8)
                       val parsedMessage = try {
                           val json = JSONObject(msgString)
                           json.optString("message", msgString)
                       } catch (e: Exception) {
                           msgString
                       }
                       _uiToast.emit(
                           "${event.participant?.name}: $parsedMessage"
                       )
                       _chatMessages.update { oldList ->
                           oldList + ChatMessage(
                               message = parsedMessage,
                               participant = event.participant,
                               timestamp = System.currentTimeMillis(),
                               senderName = event.participant?.name ?: ""
                           )
                       }
                       val messageJson = event.data.toString(Charsets.UTF_8)
                       try {
                           val payload = Gson().fromJson(messageJson, ChatPayload::class.java)
                           _chatMessages.value = _chatMessages.value + ChatMessage(
                               participant = event.participant,
                               timestamp = System.currentTimeMillis(),
                               message = payload.message,
                               senderName = payload.senderName
                           )
                       } catch (e: Exception) {

                       }
                   }
                   else -> {

                   }
               }
           }
        }
    }

    fun toggleCamera(isEnabled: Boolean) {
        viewModelScope.launch {
            _room.value?.localParticipant?.setCameraEnabled(isEnabled)
        }
    }

    fun toggleMic(isEnabled: Boolean) {
        viewModelScope.launch {
            _room.value?.localParticipant?.setMicrophoneEnabled(isEnabled)
        }
    }

    override fun onCleared() {
        super.onCleared()
        _room.value?.disconnect()
        _room.value = null
    }

    fun disconnect() {
        viewModelScope.launch {
            _room.value?.disconnect()
            _room.value = null
            _remoteVideoTracks.value = emptyList()
            _localVideoTrack.value = null
            _remoteScreenVideo.value = null
            _uiToast.emit("Leave meeting")
        }
    }
}