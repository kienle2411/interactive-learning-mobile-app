package com.se122.interactivelearning.data.remote.api

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T): ApiResult<T>()
    data class Error(
        val code: Int?,
        val message: String?,
        val errors: List<String>? = null
    ): ApiResult<Nothing>()
    data class Exception(val e: Throwable): ApiResult<Nothing>()
}