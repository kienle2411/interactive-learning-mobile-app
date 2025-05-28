package com.se122.interactivelearning.domain.usecase

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.MeetingResponse
import com.se122.interactivelearning.domain.repository.MeetingRepository
import javax.inject.Inject

class GetMeetingInformationUseCase @Inject constructor(
    private val repository: MeetingRepository
) {
    suspend operator fun invoke(id: String): ApiResult<MeetingResponse> {
        return repository.getMeeting(id)
    }
}