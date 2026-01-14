package com.se122.interactivelearning.domain.usecase.question

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.ExplainQuestionRequest
import com.se122.interactivelearning.data.remote.dto.ExplainQuestionResponse
import com.se122.interactivelearning.domain.repository.QuestionRepository
import javax.inject.Inject

class ExplainQuestionUseCase @Inject constructor(
    private val questionRepository: QuestionRepository
) {
    suspend operator fun invoke(
        id: String,
        request: ExplainQuestionRequest
    ): ApiResult<ExplainQuestionResponse> {
        return questionRepository.explainQuestion(id, request)
    }
}
