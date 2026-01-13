package com.se122.interactivelearning.domain.usecase.classroom

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.api.PaginationResponse
import com.se122.interactivelearning.data.remote.dto.LessonResponse
import com.se122.interactivelearning.domain.repository.ClassroomRepository
import javax.inject.Inject

class GetTopicLessonsUseCase @Inject constructor(
    private val repository: ClassroomRepository
) {
    suspend operator fun invoke(id: String): ApiResult<PaginationResponse<LessonResponse>> {
        return repository.getTopicLessons(id)
    }
}
