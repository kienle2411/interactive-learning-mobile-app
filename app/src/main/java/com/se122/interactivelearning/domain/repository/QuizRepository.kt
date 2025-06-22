package com.se122.interactivelearning.domain.repository

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.QuizAttemptResponse
import com.se122.interactivelearning.data.remote.dto.QuizResponse

interface QuizRepository {
    suspend fun getQuiz(id: String): ApiResult<QuizResponse>
    suspend fun attemptQuiz(id: String): ApiResult<QuizAttemptResponse>
    suspend fun attemptQuizByCode(code: String): ApiResult<QuizAttemptResponse>
}