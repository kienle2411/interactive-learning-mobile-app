package com.se122.interactivelearning.domain.repository

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.ProfileRequest
import com.se122.interactivelearning.data.remote.dto.ProfileResponse
import com.se122.interactivelearning.data.remote.dto.UploadAvatarResponse
import okhttp3.MultipartBody

interface UserRepository {
    suspend fun editProfile(body: ProfileRequest): ApiResult<ProfileResponse>
    suspend fun uploadAvatar(file: MultipartBody.Part): ApiResult<UploadAvatarResponse>
}