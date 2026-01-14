package com.se122.interactivelearning.data.repository

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.api.AuthApi
import com.se122.interactivelearning.data.remote.dto.RecoveryRequest
import com.se122.interactivelearning.data.remote.dto.RecoveryResponse
import com.se122.interactivelearning.data.remote.dto.RegisterRequest
import com.se122.interactivelearning.data.remote.dto.RegisterResponse
import com.se122.interactivelearning.domain.repository.RegisterRepository
import org.json.JSONObject
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

    override suspend fun sendRecoveryEmail(request: RecoveryRequest): ApiResult<RecoveryResponse> {
        return try {
            val response = api.sendRecoveryEmail(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.data != null) {
                    ApiResult.Success(body.data)
                } else {
                    ApiResult.Error(response.code(), "Empty data in response body")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val json = JSONObject(errorBody ?: "")
                    val errors = json.optJSONArray("errors")
                    if (errors != null && errors.length() > 0) {
                        errors.getString(0)
                    } else {
                        json.optString("message", "Unknown error")
                    }
                } catch (e: Exception) {
                    "Unknown error"
                }

                ApiResult.Error(response.code(), errorMessage)
            }
        } catch (e: Exception) {
            ApiResult.Exception(e)
        }
    }
}