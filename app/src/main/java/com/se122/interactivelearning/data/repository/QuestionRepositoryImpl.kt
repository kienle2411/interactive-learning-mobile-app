package com.se122.interactivelearning.data.repository

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.api.QuestionApi
import com.se122.interactivelearning.data.remote.api.safeApiCall
import com.se122.interactivelearning.data.remote.dto.ExplainQuestionRequest
import com.se122.interactivelearning.data.remote.dto.ExplainQuestionResponse
import com.se122.interactivelearning.data.remote.dto.QuestionResponse
import com.se122.interactivelearning.domain.repository.QuestionRepository
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(
    private val questionApi: QuestionApi
): QuestionRepository {
    override suspend fun getQuestion(id: String): ApiResult<QuestionResponse> {
        return safeApiCall {
            questionApi.getQuestion(id)
        }
    }

    override suspend fun explainQuestion(
        id: String,
        request: ExplainQuestionRequest
    ): ApiResult<ExplainQuestionResponse> {
        return safeApiCall {
            questionApi.explainQuestion(id, request)
        }
    }
}
