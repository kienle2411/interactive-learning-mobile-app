package com.se122.interactivelearning.data.repository

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.api.MeetingApi
import com.se122.interactivelearning.data.remote.api.safeApiCall
import com.se122.interactivelearning.data.remote.dto.MeetingAccessTokenResponse
import com.se122.interactivelearning.data.remote.dto.MeetingResponse
import com.se122.interactivelearning.domain.repository.MeetingRepository
import javax.inject.Inject

class MeetingRepositoryImpl @Inject constructor(
    private val meetingApi: MeetingApi
): MeetingRepository {
    override suspend fun getMeeting(id: String): ApiResult<MeetingResponse> {
        return safeApiCall {
            meetingApi.getMeeting(id)
        }
    }
    override suspend fun getMeetingAccessToken(id: String): ApiResult<MeetingAccessTokenResponse> {
        return safeApiCall {
            meetingApi.getMeetingAccessToken(id)
        }
    }
}