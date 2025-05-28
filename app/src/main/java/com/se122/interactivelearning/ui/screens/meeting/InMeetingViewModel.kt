package com.se122.interactivelearning.ui.screens.meeting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.MeetingResponse
import com.se122.interactivelearning.domain.usecase.GetMeetingInformationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.socket.client.IO
import io.socket.client.IO.socket
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InMeetingViewModel @Inject constructor(
    private val getMeetingInformationUseCase: GetMeetingInformationUseCase
): ViewModel() {

    private lateinit var socket: Socket

    private val _meeting = MutableStateFlow<ViewState<MeetingResponse>>(ViewState.Idle)
    val meeting = _meeting.asStateFlow()

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

    fun connectToMeeting(meetingId: String) {
        if (::socket.isInitialized && socket.connected()) return
//        val token = userRe

        socket = IO.socket("http:// /meeting")

        socket.on(Socket.EVENT_CONNECT) {
            socket.emit("joinMeeting", meetingId)
        }

        socket.on("meetingInfo") { args ->
//            val
        }
    }

    fun disconnect() {
        if (::socket.isInitialized && socket.connected()) {
            socket.disconnect()
            socket.off()
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
}