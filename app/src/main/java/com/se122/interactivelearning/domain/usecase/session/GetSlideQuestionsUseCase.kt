package com.se122.interactivelearning.domain.usecase.session

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.SlideQuestionResponse
import com.se122.interactivelearning.domain.repository.SessionRepository
import javax.inject.Inject

class GetSlideQuestionsUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(slidePageId: String): ApiResult<SlideQuestionResponse> {
        return sessionRepository.getSlideQuestions(slidePageId)
    }
}
