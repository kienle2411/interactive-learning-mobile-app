package com.se122.interactivelearning.ui.components.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.CreateChatThreadRequest
import com.se122.interactivelearning.data.remote.dto.SendChatMessageRequest
import com.se122.interactivelearning.domain.usecase.chat.CreateChatThreadUseCase
import com.se122.interactivelearning.domain.usecase.chat.SendChatMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AgentChatViewModel @Inject constructor(
    private val createChatThreadUseCase: CreateChatThreadUseCase,
    private val sendChatMessageUseCase: SendChatMessageUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(AgentChatState())
    val uiState = _uiState.asStateFlow()

    private var context: ChatContext? = null

    fun setContext(context: ChatContext) {
        if (this.context == context) return
        this.context = context
        _uiState.value = AgentChatState()
    }

    fun ensureThread() {
        val ctx = context
        if (ctx == null) {
            _uiState.update { it.copy(errorMessage = "Chua co thong tin ngu canh de khoi tao chat") }
            return
        }
        val state = _uiState.value
        if (state.threadId != null || state.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val request = CreateChatThreadRequest(
                title = ctx.title,
                scopeType = ctx.scopeType.name,
                classroomId = ctx.classroomId,
                lessonId = ctx.lessonId,
                assignmentId = ctx.assignmentId,
                quizId = ctx.quizId
            )
            when (val result = createChatThreadUseCase(request)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(threadId = result.data.id, isLoading = false) }
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message ?: "Khong the tao cuoc tro chuyen"
                        )
                    }
                }
                is ApiResult.Exception -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.e.message ?: "Khong the tao cuoc tro chuyen"
                        )
                    }
                }
            }
        }
    }

    fun sendMessage(content: String) {
        val trimmed = content.trim()
        if (trimmed.isEmpty()) return
        val threadId = _uiState.value.threadId
        if (threadId.isNullOrBlank()) {
            _uiState.update { it.copy(errorMessage = "Chat dang khoi tao, vui long thu lai") }
            return
        }

        val localMessage = ChatUiMessage(
            id = "local-${System.currentTimeMillis()}",
            role = ChatRole.USER,
            content = trimmed,
            isLocal = true
        )
        _uiState.update {
            it.copy(
                messages = it.messages + localMessage,
                isSending = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            val request = SendChatMessageRequest(content = trimmed)
            when (val result = sendChatMessageUseCase(threadId, request)) {
                is ApiResult.Success -> {
                    val assistantMessage = result.data.assistantMessage
                    _uiState.update {
                        it.copy(
                            isSending = false,
                            messages = it.messages + ChatUiMessage(
                                id = assistantMessage.id,
                                role = ChatRole.ASSISTANT,
                                content = assistantMessage.content,
                                createdAt = assistantMessage.createdAt
                            )
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isSending = false,
                            errorMessage = result.message ?: "Gui tin nhan that bai"
                        )
                    }
                }
                is ApiResult.Exception -> {
                    _uiState.update {
                        it.copy(
                            isSending = false,
                            errorMessage = result.e.message ?: "Gui tin nhan that bai"
                        )
                    }
                }
            }
        }
    }
}

data class AgentChatState(
    val threadId: String? = null,
    val messages: List<ChatUiMessage> = emptyList(),
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val errorMessage: String? = null
)

data class ChatUiMessage(
    val id: String,
    val role: ChatRole,
    val content: String,
    val createdAt: String? = null,
    val isLocal: Boolean = false
)

data class ChatContext(
    val scopeType: ChatScopeType,
    val classroomId: String? = null,
    val lessonId: String? = null,
    val assignmentId: String? = null,
    val quizId: String? = null,
    val title: String? = null
)

enum class ChatScopeType {
    CLASSROOM,
    LESSON,
    ASSIGNMENT,
    QUIZ
}

enum class ChatRole {
    USER,
    ASSISTANT,
    SYSTEM
}
