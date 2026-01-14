package com.se122.interactivelearning.data.repository

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.api.UserApi
import com.se122.interactivelearning.data.remote.api.safeApiCall
import com.se122.interactivelearning.data.remote.dto.ProfileRequest
import com.se122.interactivelearning.data.remote.dto.ProfileResponse
import com.se122.interactivelearning.data.remote.dto.UploadAvatarResponse
import com.se122.interactivelearning.domain.repository.UserRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
): UserRepository {
    override suspend fun editProfile(body: ProfileRequest): ApiResult<ProfileResponse> {
        return safeApiCall {
            userApi.editProfile(body)
        }
    }

    override suspend fun uploadAvatar(file: MultipartBody.Part): ApiResult<UploadAvatarResponse> {
        return safeApiCall {
            userApi.uploadAvatar(file)
        }
    }
}