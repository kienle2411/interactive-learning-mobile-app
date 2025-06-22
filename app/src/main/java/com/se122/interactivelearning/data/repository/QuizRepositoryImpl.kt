package com.se122.interactivelearning.data.repository

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.api.QuizApi
import com.se122.interactivelearning.data.remote.api.safeApiCall
import com.se122.interactivelearning.data.remote.dto.QuizAttemptResponse
import com.se122.interactivelearning.data.remote.dto.QuizResponse
import com.se122.interactivelearning.domain.repository.QuizRepository
import javax.inject.Inject

class QuizRepositoryImpl @Inject constructor(
    private val quizApi: QuizApi
): QuizRepository {
    override suspend fun getQuiz(id: String): ApiResult<QuizResponse> {
        return safeApiCall {
            quizApi.getQuiz(id)
        }
    }
    override suspend fun attemptQuiz(id: String): ApiResult<QuizAttemptResponse> {
        return safeApiCall {
            quizApi.attemptQuiz(id)
        }
    }
    override suspend fun attemptQuizByCode(code: String): ApiResult<QuizAttemptResponse> {
        return safeApiCall {
            quizApi.attemptQuizByCode(code)
        }
    }
}