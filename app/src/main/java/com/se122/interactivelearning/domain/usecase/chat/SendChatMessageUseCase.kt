package com.se122.interactivelearning.domain.usecase.chat

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.SendChatMessageRequest
import com.se122.interactivelearning.data.remote.dto.SendChatMessageResponse
import com.se122.interactivelearning.domain.repository.ChatRepository
import javax.inject.Inject

class SendChatMessageUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(
        threadId: String,
        request: SendChatMessageRequest
    ): ApiResult<SendChatMessageResponse> {
        return repository.sendMessage(threadId, request)
    }
}
