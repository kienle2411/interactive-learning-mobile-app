package com.se122.interactivelearning.data.remote.api

import com.se122.interactivelearning.data.remote.dto.LoginRequest
import com.se122.interactivelearning.data.remote.dto.LoginResponse
import com.se122.interactivelearning.data.remote.dto.ProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/signin")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<LoginResponse>>

    @GET("users/profile")
    suspend fun getProfile(): Response<ApiResponse<ProfileResponse>>

    @GET("auth/refresh")
    suspend fun refresh(@Header("Authorization") token: String): Response<ApiResponse<LoginResponse>>
}