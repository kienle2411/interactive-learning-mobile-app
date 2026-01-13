package com.se122.interactivelearning.domain.usecase.chat

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.ChatThreadResponse
import com.se122.interactivelearning.data.remote.dto.CreateChatThreadRequest
import com.se122.interactivelearning.domain.repository.ChatRepository
import javax.inject.Inject

class CreateChatThreadUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(request: CreateChatThreadRequest): ApiResult<ChatThreadResponse> {
        return repository.createThread(request)
    }
}
