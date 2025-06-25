package com.se122.interactivelearning.data.remote.api

import com.se122.interactivelearning.data.remote.dto.LoginRequest
import com.se122.interactivelearning.data.remote.dto.LoginResponse
import com.se122.interactivelearning.data.remote.dto.ProfileResponse
import com.se122.interactivelearning.data.remote.dto.RecoveryRequest
import com.se122.interactivelearning.data.remote.dto.RecoveryResponse
import com.se122.interactivelearning.data.remote.dto.RegisterRequest
import com.se122.interactivelearning.data.remote.dto.RegisterResponse
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

    @POST("auth/signup")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<RegisterResponse>>

    @POST("auth/forgot-password")
    suspend fun sendRecoveryEmail(
        @Body request: RecoveryRequest
    ): Response<ApiResponse<RecoveryResponse>>

    @GET("users/profile")
    suspend fun getProfile(): Response<ApiResponse<ProfileResponse>>

    @GET("auth/refresh")
    suspend fun refresh(@Header("Authorization") token: String): Response<ApiResponse<LoginResponse>>
}