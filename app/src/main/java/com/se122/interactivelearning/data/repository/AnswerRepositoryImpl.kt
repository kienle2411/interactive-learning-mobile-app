package com.se122.interactivelearning.data.repository

import com.se122.interactivelearning.data.remote.api.AnswerApi
import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.api.safeApiCall
import com.se122.interactivelearning.data.remote.dto.AnswerRequest
import com.se122.interactivelearning.data.remote.dto.AnswerResponse
import com.se122.interactivelearning.domain.repository.AnswerRepository
import javax.inject.Inject

class AnswerRepositoryImpl @Inject constructor(
    private val answerApi: AnswerApi
): AnswerRepository {
    override suspend fun submitAnswer(answerRequest: AnswerRequest): ApiResult<AnswerResponse> {
        return safeApiCall {
            answerApi.submitAnswer(answerRequest)
        }
    }
}