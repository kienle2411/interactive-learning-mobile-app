package com.se122.interactivelearning.domain.usecase.quiz

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.QuizResponse
import com.se122.interactivelearning.domain.repository.QuizRepository
import javax.inject.Inject

class GetQuizInformationUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend operator fun invoke(id: String): ApiResult<QuizResponse> {
        return quizRepository.getQuiz(id)
    }
}