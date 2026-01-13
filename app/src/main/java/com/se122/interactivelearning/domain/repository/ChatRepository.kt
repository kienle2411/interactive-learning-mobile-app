package com.se122.interactivelearning.domain.repository

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.ChatThreadResponse
import com.se122.interactivelearning.data.remote.dto.CreateChatThreadRequest
import com.se122.interactivelearning.data.remote.dto.SendChatMessageRequest
import com.se122.interactivelearning.data.remote.dto.SendChatMessageResponse

interface ChatRepository {
    suspend fun createThread(request: CreateChatThreadRequest): ApiResult<ChatThreadResponse>
    suspend fun listThreads(classroomId: String?): ApiResult<List<ChatThreadResponse>>
    suspend fun getThread(threadId: String): ApiResult<ChatThreadResponse>
    suspend fun sendMessage(
        threadId: String,
        request: SendChatMessageRequest
    ): ApiResult<SendChatMessageResponse>
}
