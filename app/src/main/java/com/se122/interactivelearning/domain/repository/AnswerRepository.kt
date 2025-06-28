package com.se122.interactivelearning.domain.repository

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.AnswerRequest
import com.se122.interactivelearning.data.remote.dto.AnswerResponse

interface AnswerRepository {
    suspend fun submitAnswer(answerRequest: AnswerRequest): ApiResult<AnswerResponse>
}