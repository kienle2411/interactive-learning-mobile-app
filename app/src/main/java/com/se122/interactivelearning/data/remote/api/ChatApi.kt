package com.se122.interactivelearning.data.remote.api

import com.se122.interactivelearning.data.remote.dto.ChatThreadResponse
import com.se122.interactivelearning.data.remote.dto.CreateChatThreadRequest
import com.se122.interactivelearning.data.remote.dto.SendChatMessageRequest
import com.se122.interactivelearning.data.remote.dto.SendChatMessageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatApi {
    @POST("chat/threads")
    suspend fun createThread(
        @Body request: CreateChatThreadRequest
    ): Response<ApiResponse<ChatThreadResponse>>

    @GET("chat/threads")
    suspend fun listThreads(
        @Query("classroomId") classroomId: String?
    ): Response<ApiResponse<List<ChatThreadResponse>>>

    @GET("chat/threads/{id}")
    suspend fun getThread(
        @Path("id") threadId: String
    ): Response<ApiResponse<ChatThreadResponse>>

    @POST("chat/threads/{id}/messages")
    suspend fun sendMessage(
        @Path("id") threadId: String,
        @Body request: SendChatMessageRequest
    ): Response<ApiResponse<SendChatMessageResponse>>
}
