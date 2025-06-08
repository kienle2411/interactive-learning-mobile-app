package com.se122.interactivelearning.domain.repository

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.MeetingAccessTokenResponse
import com.se122.interactivelearning.data.remote.dto.MeetingResponse

interface MeetingRepository {
    suspend fun getMeeting(id: String): ApiResult<MeetingResponse>
    suspend fun getMeetingAccessToken(id: String): ApiResult<MeetingAccessTokenResponse>
}