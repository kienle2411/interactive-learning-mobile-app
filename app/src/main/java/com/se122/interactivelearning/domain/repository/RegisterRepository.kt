package com.se122.interactivelearning.domain.repository

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.RegisterRequest
import com.se122.interactivelearning.data.remote.dto.RegisterResponse

interface RegisterRepository {
    suspend fun register(request: RegisterRequest): ApiResult<RegisterResponse>
}