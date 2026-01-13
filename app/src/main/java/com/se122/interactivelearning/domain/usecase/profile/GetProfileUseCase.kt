package com.se122.interactivelearning.domain.usecase.profile

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.ProfileResponse
import com.se122.interactivelearning.domain.repository.LoginRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(): ApiResult<ProfileResponse> {
        return repository.getProfile()
    }
}