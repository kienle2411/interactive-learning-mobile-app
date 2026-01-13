package com.se122.interactivelearning.data.repository

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.api.ChatApi
import com.se122.interactivelearning.data.remote.api.safeApiCall
import com.se122.interactivelearning.data.remote.dto.ChatThreadResponse
import com.se122.interactivelearning.data.remote.dto.CreateChatThreadRequest
import com.se122.interactivelearning.data.remote.dto.SendChatMessageRequest
import com.se122.interactivelearning.data.remote.dto.SendChatMessageResponse
import com.se122.interactivelearning.domain.repository.ChatRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi
) : ChatRepository {
    override suspend fun createThread(
        request: CreateChatThreadRequest
    ): ApiResult<ChatThreadResponse> {
        return safeApiCall {
            chatApi.createThread(request)
        }
    }

    override suspend fun listThreads(classroomId: String?): ApiResult<List<ChatThreadResponse>> {
        return safeApiCall {
            chatApi.listThreads(classroomId)
        }
    }

    override suspend fun getThread(threadId: String): ApiResult<ChatThreadResponse> {
        return safeApiCall {
            chatApi.getThread(threadId)
        }
    }

    override suspend fun sendMessage(
        threadId: String,
        request: SendChatMessageRequest
    ): ApiResult<SendChatMessageResponse> {
        return safeApiCall {
            chatApi.sendMessage(threadId, request)
        }
    }
}
