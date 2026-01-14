package com.se122.interactivelearning.data.repository

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.api.SessionApi
import com.se122.interactivelearning.data.remote.api.safeApiCall
import com.se122.interactivelearning.data.remote.dto.SessionResponse
import com.se122.interactivelearning.domain.repository.SessionRepository
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val sessionApi: SessionApi
): SessionRepository {
    override suspend fun getSession(id: String): ApiResult<SessionResponse> {
        return safeApiCall {
            sessionApi.getSession(id)
        }
    }
}