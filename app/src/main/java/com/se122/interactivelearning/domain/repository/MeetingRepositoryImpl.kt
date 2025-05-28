package com.se122.interactivelearning.domain.repository

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.api.MeetingApi
import com.se122.interactivelearning.data.remote.api.safeApiCall
import com.se122.interactivelearning.data.remote.dto.MeetingResponse
import javax.inject.Inject

class MeetingRepositoryImpl @Inject constructor(
    private val meetingApi: MeetingApi
): MeetingRepository {
    override suspend fun getMeeting(id: String): ApiResult<MeetingResponse> {
        return safeApiCall {
            meetingApi.getMeeting(id)
        }
    }
}