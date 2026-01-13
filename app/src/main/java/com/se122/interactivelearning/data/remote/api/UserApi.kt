package com.se122.interactivelearning.data.remote.api

import com.se122.interactivelearning.data.remote.dto.ProfileRequest
import com.se122.interactivelearning.data.remote.dto.ProfileResponse
import com.se122.interactivelearning.data.remote.dto.UploadAvatarResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part

interface UserApi {
    @PATCH("users/profile")
    suspend fun editProfile(
        @Body request: ProfileRequest
    ): Response<ApiResponse<ProfileResponse>>

    @Multipart
    @POST("users/avatar")
    suspend fun uploadAvatar(
        @Part file: MultipartBody.Part
    ): Response<ApiResponse<UploadAvatarResponse>>
}