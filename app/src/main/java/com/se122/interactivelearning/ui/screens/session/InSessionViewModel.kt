package com.se122.interactivelearning.ui.screens.session

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.AnswerRequest
import com.se122.interactivelearning.data.remote.dto.AnswerResponse
import com.se122.interactivelearning.data.remote.dto.QuestionResponse
import com.se122.interactivelearning.data.remote.dto.SessionResponse
import com.se122.interactivelearning.domain.model.ChatMessage
import com.se122.interactivelearning.domain.model.ChatMessageSession
import com.se122.interactivelearning.domain.repository.SessionSocketRepository
import com.se122.interactivelearning.domain.usecase.answer.CreateAnswerUseCase
import com.se122.interactivelearning.domain.usecase.question.GetQuestionUseCase
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
    private val getQuestionUseCase: GetQuestionUseCase,
    private val createAnswerUseCase: CreateAnswerUseCase
): ViewModel() {
    private val _session = MutableStateFlow<ViewState<SessionResponse>>(ViewState.Loading)
    val session = _session.asStateFlow()

    private val _slideUrl = MutableStateFlow<String?>(null)
    val slideUrl = _slideUrl.asStateFlow()

    private val _slidePageId = MutableStateFlow<String?>(null)
    val slidePageId = _slidePageId.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessageSession>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _question = MutableStateFlow<ViewState<QuestionResponse>>(ViewState.Idle)
    val question = _question.asStateFlow()

    private val _answer = MutableStateFlow<ViewState<AnswerResponse>>(ViewState.Idle)
    val answer = _answer.asStateFlow()

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
            observeQuestion()
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
        sessionSocketRepository.onSlideReceived { slideUrl, slidePageId ->
            _slideUrl.value = slideUrl
            _slidePageId.value = slidePageId
            resetQuestion()
            resetAnswer()
        }
    }

    fun observeMessages() {
        sessionSocketRepository.onMessageReceived {
            _messages.value = _messages.value + it
        }
    }

    fun observeQuestion() {
        sessionSocketRepository.onQuestionReceived {
            Log.d("InSessionViewModel", "Question received: $it")
            getQuestion(it)
        }
    }

    fun getQuestion(id: String) {
        viewModelScope.launch {
            _question.value = ViewState.Loading
            when (val result = getQuestionUseCase(id)) {
                is ApiResult.Success -> {
                    _question.value = ViewState.Success(result.data)
                    Log.d("InSessionViewModel", "Question: ${result.data}")
                    Log.d("InSessionViewModel", "Question: ${_question.value}")
                }
                else -> {

                }
            }
        }
    }

    fun resetQuestion() {
        viewModelScope.launch {
            _question.value = ViewState.Idle
        }
    }

    fun resetAnswer() {
        viewModelScope.launch {
            _answer.value = ViewState.Idle
        }
    }

    fun sendMessage(sessionId: String, message: String) {
        sessionSocketRepository.sendMessage(sessionId, message)
        _messages.value = _messages.value + ChatMessageSession("", "You", message, System.currentTimeMillis())
    }

    fun createAnswer(answerRequest: AnswerRequest) {
        viewModelScope.launch {
            _answer.value = ViewState.Loading
            when (val result = createAnswerUseCase(answerRequest)) {
                is ApiResult.Success -> {
                    _answer.value = ViewState.Success(result.data)
                }
                else -> {

                }
            }
        }

    }

    override fun onCleared() {
        sessionSocketRepository.disconnect()
        super.onCleared()
    }
}