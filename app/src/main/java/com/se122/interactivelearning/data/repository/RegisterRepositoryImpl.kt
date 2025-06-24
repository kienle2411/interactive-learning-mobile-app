package com.se122.interactivelearning.data.repository

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.api.AuthApi
import com.se122.interactivelearning.data.remote.dto.RegisterRequest
import com.se122.interactivelearning.data.remote.dto.RegisterResponse
import com.se122.interactivelearning.domain.repository.RegisterRepository
import javax.inject.Inject

class RegisterRepositoryImpl @Inject constructor(
    private val api: AuthApi
) : RegisterRepository {

    override suspend fun register(request: RegisterRequest): ApiResult<RegisterResponse> {
        return try {
            val response = api.register(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.data != null) {
                    ApiResult.Success(body.data)
                } else {
                    ApiResult.Error(response.code(), "Empty data in response body")
                }
            } else {
                ApiResult.Error(response.code(), response.errorBody()?.string() ?: "Unknown error")
            }
        } catch (e: Exception) {
            ApiResult.Exception(e)
        }
    }
}