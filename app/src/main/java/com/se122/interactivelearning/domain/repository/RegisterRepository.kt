package com.se122.interactivelearning.domain.repository

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.RecoveryRequest
import com.se122.interactivelearning.data.remote.dto.RecoveryResponse
import com.se122.interactivelearning.data.remote.dto.RegisterRequest
import com.se122.interactivelearning.data.remote.dto.RegisterResponse

interface RegisterRepository {
    suspend fun register(request: RegisterRequest): ApiResult<RegisterResponse>
    suspend fun sendRecoveryEmail(request: RecoveryRequest): ApiResult<RecoveryResponse>
}