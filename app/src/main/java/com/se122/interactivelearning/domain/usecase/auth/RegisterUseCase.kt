package com.se122.interactivelearning.domain.usecase.auth

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.RegisterRequest
import com.se122.interactivelearning.data.remote.dto.RegisterResponse
import com.se122.interactivelearning.domain.repository.RegisterRepository
import javax.inject.Inject


class RegisterUseCase @Inject constructor(
    private val repository: RegisterRepository
) {
    suspend operator fun invoke(request: RegisterRequest): ApiResult<RegisterResponse> {
        return repository.register(request)
    }
}