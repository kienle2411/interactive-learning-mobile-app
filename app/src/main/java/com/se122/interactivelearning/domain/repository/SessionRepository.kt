package com.se122.interactivelearning.domain.repository

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.SessionResponse
import com.se122.interactivelearning.data.remote.dto.SlideQuestionResponse

interface SessionRepository {
    suspend fun getSession(id: String): ApiResult<SessionResponse>
    suspend fun getSlideQuestions(slidePageId: String): ApiResult<SlideQuestionResponse>
}
