package com.se122.interactivelearning.domain.usecase.auth

import android.content.Context
import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.LoginResponse
import com.se122.interactivelearning.domain.repository.LoginRepository
import com.se122.interactivelearning.utils.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: LoginRepository,
    private val context: Context
) {
    private val tokenManager = TokenManager(context)
    suspend operator fun invoke(username: String, password: String): ApiResult<LoginResponse> {
        val result = repository.login(username, password)
        if (result is ApiResult.Success) {
            val accessToken = result.data.accessToken;
            val refreshToken = result.data.refreshToken;
            withContext(Dispatchers.IO) {
                tokenManager.saveTokens(accessToken, refreshToken)
            }
        }
        return result
    }
}