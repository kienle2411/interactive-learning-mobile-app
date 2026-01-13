package com.se122.interactivelearning.domain.usecase.classroom

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.LessonDetailResponse
import com.se122.interactivelearning.domain.repository.ClassroomRepository
import javax.inject.Inject

class GetLessonDetailUseCase @Inject constructor(
    private val repository: ClassroomRepository
) {
    suspend operator fun invoke(id: String): ApiResult<LessonDetailResponse> {
        return repository.getLessonDetail(id)
    }
}
