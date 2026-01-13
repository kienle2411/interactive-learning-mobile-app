package com.se122.interactivelearning.domain.repository

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.QuestionResponse

interface QuestionRepository {
    suspend fun getQuestion(id: String): ApiResult<QuestionResponse>
}