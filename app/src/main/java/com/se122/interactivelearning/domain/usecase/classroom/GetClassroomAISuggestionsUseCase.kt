package com.se122.interactivelearning.domain.usecase.classroom

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.SuggestionsResponse
import com.se122.interactivelearning.domain.repository.ClassroomRepository
import javax.inject.Inject

class GetClassroomAISuggestionsUseCase @Inject constructor(
    private val repository: ClassroomRepository
) {
    suspend operator fun invoke(classroomId: String): ApiResult<SuggestionsResponse> {
        return repository.getClassroomAISuggestions(classroomId)
    }
}
