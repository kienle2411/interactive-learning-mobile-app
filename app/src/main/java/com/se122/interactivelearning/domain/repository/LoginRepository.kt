package com.se122.interactivelearning.domain.repository

import com.se122.interactivelearning.data.remote.api.ApiResponse
import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.api.AuthApi
import com.se122.interactivelearning.data.remote.api.safeApiCall
import com.se122.interactivelearning.data.remote.dto.LoginRequest
import com.se122.interactivelearning.data.remote.dto.LoginResponse
import com.se122.interactivelearning.data.remote.dto.ProfileResponse
import retrofit2.Response
import javax.inject.Inject

interface LoginRepository {
    suspend fun login(username: String, password: String): ApiResult<LoginResponse>
    suspend fun getProfile(): ApiResult<ProfileResponse>
}