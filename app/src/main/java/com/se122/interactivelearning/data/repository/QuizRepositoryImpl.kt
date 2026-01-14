package com.se122.interactivelearning.data.repository

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.api.QuizApi
import com.se122.interactivelearning.data.remote.api.safeApiCall
import com.se122.interactivelearning.data.remote.dto.QuizAnswerResponse
import com.se122.interactivelearning.data.remote.dto.QuizAttemptQuestionItem
import com.se122.interactivelearning.data.remote.dto.QuizAttemptResponse
import com.se122.interactivelearning.data.remote.dto.QuizResponse
import com.se122.interactivelearning.data.remote.dto.SubmitQuizAnswerRequest
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

    override suspend fun getMyQuizAttempt(id: String): ApiResult<QuizAttemptResponse> {
        return safeApiCall {
            quizApi.getMyQuizAttempt(id)
        }
    }

    override suspend fun getAttemptQuestions(
        attemptId: String
    ): ApiResult<List<QuizAttemptQuestionItem>> {
        return safeApiCall {
            quizApi.getAttemptQuestions(attemptId)
        }
    }

    override suspend fun submitQuizAnswer(
        attemptId: String,
        request: SubmitQuizAnswerRequest
    ): ApiResult<QuizAnswerResponse> {
        return safeApiCall {
            quizApi.submitQuizAnswer(attemptId, request)
        }
    }

    override suspend fun submitQuizAttempt(attemptId: String): ApiResult<QuizAttemptResponse> {
        return safeApiCall {
            quizApi.submitQuizAttempt(attemptId)
        }
    }

    override suspend fun getQuizAttempts(id: String): ApiResult<List<QuizAttemptResponse>> {
        return safeApiCall {
            quizApi.getQuizAttempts(id)
        }
    }
}
