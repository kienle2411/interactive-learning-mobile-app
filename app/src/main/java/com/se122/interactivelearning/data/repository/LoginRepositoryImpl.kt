package com.se122.interactivelearning.data.repository

import android.util.Log
import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.api.AuthApi
import com.se122.interactivelearning.data.remote.api.safeApiCall
import com.se122.interactivelearning.data.remote.dto.LoginRequest
import com.se122.interactivelearning.data.remote.dto.LoginResponse
import com.se122.interactivelearning.data.remote.dto.ProfileResponse
import com.se122.interactivelearning.domain.repository.LoginRepository
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val authApi: AuthApi
): LoginRepository {
    override suspend fun login(username: String, password: String): ApiResult<LoginResponse> {
        Log.i("Repo", "Login is called")
        return safeApiCall {
            authApi.login(LoginRequest(username, password))
        }
    }

    override suspend fun getProfile(): ApiResult<ProfileResponse> {
        return safeApiCall {
            authApi.getProfile()
        }
    }
}