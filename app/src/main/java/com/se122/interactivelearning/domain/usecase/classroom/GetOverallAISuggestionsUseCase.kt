package com.se122.interactivelearning.domain.usecase.classroom

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.SuggestionsResponse
import com.se122.interactivelearning.domain.repository.ClassroomRepository
import javax.inject.Inject

class GetOverallAISuggestionsUseCase @Inject constructor(
    private val repository: ClassroomRepository
) {
    suspend operator fun invoke(): ApiResult<SuggestionsResponse> {
        return repository.getOverallAISuggestions()
    }
}
