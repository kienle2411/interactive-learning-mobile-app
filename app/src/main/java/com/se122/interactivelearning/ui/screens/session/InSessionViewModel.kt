package com.se122.interactivelearning.ui.screens.session

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.SessionResponse
import com.se122.interactivelearning.domain.model.ChatMessage
import com.se122.interactivelearning.domain.model.ChatMessageSession
import com.se122.interactivelearning.domain.repository.SessionSocketRepository
import com.se122.interactivelearning.domain.usecase.session.GetSessionInformationUseCase
import com.se122.interactivelearning.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InSessionViewModel @Inject constructor(
    private val sessionSocketRepository: SessionSocketRepository,
    private val getSessionInformationUseCase: GetSessionInformationUseCase,
): ViewModel() {
    private val _session = MutableStateFlow<ViewState<SessionResponse>>(ViewState.Loading)
    val session = _session.asStateFlow()

    private val _slideUrl = MutableStateFlow<String?>(null)
    val slideUrl = _slideUrl.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessageSession>>(emptyList())
    val messages = _messages.asStateFlow()

    fun getSession(id: String) {
        viewModelScope.launch {
            _session.value = ViewState.Loading
            when (val result = getSessionInformationUseCase(id)) {
                is ApiResult.Success -> {
                    _session.value = ViewState.Success(result.data)
                }
                else -> {}
            }
        }
    }

    fun connectSocket() {
        sessionSocketRepository.connect {
            observeSlide()
            observeMessages()
        }
    }

    fun disconnectSocket() {
        sessionSocketRepository.disconnect()
    }

    fun leaveSession(sessionId: String) {
        sessionSocketRepository.leaveSession(sessionId)
        disconnectSocket()
    }

    fun joinSession(sessionId: String) {
        sessionSocketRepository.joinSession(sessionId)
        Log.d("InSessionViewModel", "Joined session: $sessionId")
    }

    fun observeSlide() {
        sessionSocketRepository.onSlideReceived { slideUrl ->
            _slideUrl.value = slideUrl
        }
    }

    fun observeMessages() {
        sessionSocketRepository.onMessageReceived {
            _messages.value = _messages.value + it
        }
    }

    fun sendMessage(sessionId: String, message: String) {
        sessionSocketRepository.sendMessage(sessionId, message)
        _messages.value = _messages.value + ChatMessageSession("", "You", message, System.currentTimeMillis())
    }

    override fun onCleared() {
        sessionSocketRepository.disconnect()
        super.onCleared()
    }
}