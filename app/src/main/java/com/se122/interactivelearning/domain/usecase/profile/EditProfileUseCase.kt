package com.se122.interactivelearning.domain.usecase.profile

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.ProfileRequest
import com.se122.interactivelearning.data.remote.dto.ProfileResponse
import com.se122.interactivelearning.domain.repository.UserRepository
import javax.inject.Inject

class EditProfileUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(body: ProfileRequest): ApiResult<ProfileResponse> {
        return repository.editProfile(body)
    }
}