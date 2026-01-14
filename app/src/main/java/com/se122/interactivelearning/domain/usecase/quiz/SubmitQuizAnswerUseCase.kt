package com.se122.interactivelearning.domain.usecase.quiz

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.QuizAnswerResponse
import com.se122.interactivelearning.data.remote.dto.SubmitQuizAnswerRequest
import com.se122.interactivelearning.domain.repository.QuizRepository
import javax.inject.Inject

class SubmitQuizAnswerUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend operator fun invoke(
        attemptId: String,
        request: SubmitQuizAnswerRequest
    ): ApiResult<QuizAnswerResponse> {
        return quizRepository.submitQuizAnswer(attemptId, request)
    }
}
