package com.se122.interactivelearning.data.remote.api

data class ApiResponse<T>(
    val status: String?,
    val statusCode: String?,
    val data: T?,
    val message: String?,
    val errors: List<String>?
)

data class PaginationResponse<T>(
    val data: List<T>,
    val total: Int,
    val page: Int,
    val lastPage: Int,
)