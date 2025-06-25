package com.se122.interactivelearning.domain.usecase.auth

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.RecoveryRequest
import com.se122.interactivelearning.data.remote.dto.RecoveryResponse
import com.se122.interactivelearning.domain.repository.RegisterRepository
import javax.inject.Inject

class SendRecoveryEmailUseCase @Inject constructor(
    private val repository: RegisterRepository
) {
    suspend operator fun invoke(request: RecoveryRequest): ApiResult<RecoveryResponse> {
        return repository.sendRecoveryEmail(request)
    }
}