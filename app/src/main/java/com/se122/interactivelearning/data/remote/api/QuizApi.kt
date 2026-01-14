package com.se122.interactivelearning.data.remote.api

import com.se122.interactivelearning.data.remote.dto.QuizAnswerResponse
import com.se122.interactivelearning.data.remote.dto.QuizAttemptQuestionItem
import com.se122.interactivelearning.data.remote.dto.QuizAttemptResponse
import com.se122.interactivelearning.data.remote.dto.QuizResponse
import com.se122.interactivelearning.data.remote.dto.SubmitQuizAnswerRequest
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Body

interface QuizApi {
    @GET("quiz/{id}")
    suspend fun getQuiz(@Path("id") id: String): Response<ApiResponse<QuizResponse>>

    @POST("quiz/{id}/attempt")
    suspend fun attemptQuiz(@Path("id") id: String): Response<ApiResponse<QuizAttemptResponse>>

    @POST("quiz/code/{code}/attempt")
    suspend fun attemptQuizByCode(@Path("code") code: String): Response<ApiResponse<QuizAttemptResponse>>

    @GET("quiz/{id}/attempts/me")
    suspend fun getMyQuizAttempt(@Path("id") id: String): Response<ApiResponse<QuizAttemptResponse>>

    @GET("quiz/attempts/{attemptId}/questions")
    suspend fun getAttemptQuestions(
        @Path("attemptId") attemptId: String
    ): Response<ApiResponse<List<QuizAttemptQuestionItem>>>

    @POST("quiz/attempts/{attemptId}/answers")
    suspend fun submitQuizAnswer(
        @Path("attemptId") attemptId: String,
        @Body request: SubmitQuizAnswerRequest
    ): Response<ApiResponse<QuizAnswerResponse>>

    @POST("quiz/attempts/{attemptId}/submit")
    suspend fun submitQuizAttempt(@Path("attemptId") attemptId: String): Response<ApiResponse<QuizAttemptResponse>>

    @GET("quiz/{id}/attempts")
    suspend fun getQuizAttempts(@Path("id") id: String): Response<ApiResponse<List<QuizAttemptResponse>>>
}
