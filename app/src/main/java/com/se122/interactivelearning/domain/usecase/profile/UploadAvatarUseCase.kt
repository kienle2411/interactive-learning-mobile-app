package com.se122.interactivelearning.domain.usecase.profile

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.UploadAvatarResponse
import com.se122.interactivelearning.domain.repository.UserRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class UploadAvatarUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(file: MultipartBody.Part): ApiResult<UploadAvatarResponse> {
        return repository.uploadAvatar(file)
    }
}