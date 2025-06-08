package com.se122.interactivelearning.domain.usecase.meeting

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.MeetingAccessTokenResponse
import com.se122.interactivelearning.domain.repository.MeetingRepository
import javax.inject.Inject

class GetMeetingAccessTokenUseCase @Inject constructor(
    private val repository: MeetingRepository
) {
    suspend operator fun invoke(id: String): ApiResult<MeetingAccessTokenResponse> {
        return repository.getMeetingAccessToken(id)
    }
}