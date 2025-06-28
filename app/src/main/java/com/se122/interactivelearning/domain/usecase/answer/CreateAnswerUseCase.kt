package com.se122.interactivelearning.domain.usecase.answer

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.AnswerRequest
import com.se122.interactivelearning.data.remote.dto.AnswerResponse
import com.se122.interactivelearning.domain.repository.AnswerRepository
import javax.inject.Inject

class CreateAnswerUseCase @Inject constructor(
    private val answerRepository: AnswerRepository
) {
    suspend operator fun invoke(answerRequest: AnswerRequest): ApiResult<AnswerResponse> {
        return answerRepository.submitAnswer(answerRequest)
    }
}