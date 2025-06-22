package com.se122.interactivelearning.data.remote.api

import com.se122.interactivelearning.data.remote.dto.QuizAttemptResponse
import com.se122.interactivelearning.data.remote.dto.QuizResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface QuizApi {
    @GET("quiz/{id}")
    suspend fun getQuiz(@Path("id") id: String): Response<ApiResponse<QuizResponse>>

    @POST("quiz/{id}/attempt")
    suspend fun attemptQuiz(@Path("id") id: String): Response<ApiResponse<QuizAttemptResponse>>

    @POST("quiz/{code}/attempt")
    suspend fun attemptQuizByCode(@Path("code") code: String): Response<ApiResponse<QuizAttemptResponse>>
}