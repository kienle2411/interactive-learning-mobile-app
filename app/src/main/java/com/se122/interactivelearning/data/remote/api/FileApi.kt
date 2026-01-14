package com.se122.interactivelearning.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface FileApi {
    @GET("files/{id}/download")
    suspend fun download(@Path("id") id: String): Response<ApiResponse<String>>
}