package com.se122.interactivelearning.data.remote.api

import com.se122.interactivelearning.data.remote.dto.AnswerRequest
import com.se122.interactivelearning.data.remote.dto.AnswerResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AnswerApi {
    @POST("answers")
    suspend fun submitAnswer(@Body answerRequest: AnswerRequest): Response<ApiResponse<AnswerResponse>>
}