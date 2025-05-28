package com.se122.interactivelearning.data.remote.api

import android.util.Log
import retrofit2.Response

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<ApiResponse<T>>): ApiResult<T> {
    return try {
        val response = apiCall()
        Log.i("Response", "Response: $response")
        if (response.isSuccessful) {
            val body = response.body()
            if (body?.status == "success" && body.data != null) {
                ApiResult.Success(body.data)
            } else {
                ApiResult.Error(
                    code = body?.statusCode?.toInt() ?: response.code().toInt(),
                    message = body?.message ?: "Unknown error",
                    errors = body?.errors
                )
            }
        } else {
            Log.i("Response", "Response: ${response.errorBody().toString()}")
            val errorBody = response.errorBody()?.string()
            val gson = com.google.gson.Gson()
            val errorResponse = gson.fromJson(errorBody, ApiResponse::class.java)
            ApiResult.Error(
                code = errorResponse?.statusCode?.toInt(),
                message = errorResponse?.message?.toString(),
                errors = errorResponse?.errors
            )
        }
    } catch (e: Exception) {
        Log.i("sfl", "slf")
        ApiResult.Exception(e)
    }
}