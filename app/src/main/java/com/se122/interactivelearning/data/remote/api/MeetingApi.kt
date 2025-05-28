package com.se122.interactivelearning.data.remote.api

import com.se122.interactivelearning.data.remote.dto.MeetingResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MeetingApi {
    @GET("meetings/{id}")
    suspend fun getMeeting(@Path("id") id: String): Response<ApiResponse<MeetingResponse>>
}