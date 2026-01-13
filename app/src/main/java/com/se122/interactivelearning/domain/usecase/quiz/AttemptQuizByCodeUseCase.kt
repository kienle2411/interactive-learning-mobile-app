package com.se122.interactivelearning.domain.usecase.quiz

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.QuizAttemptResponse
import com.se122.interactivelearning.domain.repository.QuizRepository
import javax.inject.Inject

class AttemptQuizByCodeUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend operator fun invoke(code: String): ApiResult<QuizAttemptResponse> {
        return quizRepository.attemptQuizByCode(code)
    }
}