package com.se122.interactivelearning.domain.repository

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.QuizAnswerResponse
import com.se122.interactivelearning.data.remote.dto.QuizAttemptQuestionItem
import com.se122.interactivelearning.data.remote.dto.QuizAttemptResponse
import com.se122.interactivelearning.data.remote.dto.QuizResponse
import com.se122.interactivelearning.data.remote.dto.SubmitQuizAnswerRequest

interface QuizRepository {
    suspend fun getQuiz(id: String): ApiResult<QuizResponse>
    suspend fun attemptQuiz(id: String): ApiResult<QuizAttemptResponse>
    suspend fun attemptQuizByCode(code: String): ApiResult<QuizAttemptResponse>
    suspend fun getMyQuizAttempt(id: String): ApiResult<QuizAttemptResponse>
    suspend fun getAttemptQuestions(attemptId: String): ApiResult<List<QuizAttemptQuestionItem>>
    suspend fun submitQuizAnswer(
        attemptId: String,
        request: SubmitQuizAnswerRequest
    ): ApiResult<QuizAnswerResponse>
    suspend fun submitQuizAttempt(attemptId: String): ApiResult<QuizAttemptResponse>
    suspend fun getQuizAttempts(id: String): ApiResult<List<QuizAttemptResponse>>
}
