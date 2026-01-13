package com.se122.interactivelearning.domain.usecase.session

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.SessionResponse
import com.se122.interactivelearning.domain.repository.SessionRepository
import javax.inject.Inject

class GetSessionInformationUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(id: String): ApiResult<SessionResponse> {
        return sessionRepository.getSession(id)
    }
}