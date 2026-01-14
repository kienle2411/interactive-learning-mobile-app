package com.se122.interactivelearning.data.remote.api

import com.se122.interactivelearning.data.remote.dto.SessionResponse
import com.se122.interactivelearning.data.remote.dto.SlideQuestionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface SessionApi {
    @GET("sessions/{id}")
    suspend fun getSession(@Path("id") id: String): Response<ApiResponse<SessionResponse>>

    @GET("sessions/slide/{id}/questions")
    suspend fun getSlideQuestions(
        @Path("id") slidePageId: String
    ): Response<ApiResponse<SlideQuestionResponse>>
}
