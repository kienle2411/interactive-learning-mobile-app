package com.se122.interactivelearning.data.remote.api

import com.se122.interactivelearning.data.remote.dto.QuestionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface QuestionApi {
    @GET("questions/{id}")
    suspend fun getQuestion(@Path("id") id: String): Response<ApiResponse<QuestionResponse>>
}