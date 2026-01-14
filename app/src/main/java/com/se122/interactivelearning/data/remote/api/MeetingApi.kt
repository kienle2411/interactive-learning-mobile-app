package com.se122.interactivelearning.data.remote.api

import com.se122.interactivelearning.data.remote.dto.MeetingAccessTokenResponse
import com.se122.interactivelearning.data.remote.dto.MeetingResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MeetingApi {
    @GET("meetings/{id}")
    suspend fun getMeeting(@Path("id") id: String): Response<ApiResponse<MeetingResponse>>

    @GET("meetings/{id}/access-token")
    suspend fun getMeetingAccessToken(@Path("id") id: String): Response<ApiResponse<MeetingAccessTokenResponse>>
}