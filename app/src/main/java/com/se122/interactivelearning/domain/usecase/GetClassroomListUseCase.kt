package com.se122.interactivelearning.domain.usecase

import android.util.Log
import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.api.PaginationResponse
import com.se122.interactivelearning.data.remote.dto.ClassroomWrapperResponse
import com.se122.interactivelearning.domain.repository.ClassroomRepository
import javax.inject.Inject

class GetClassroomListUseCase @Inject constructor(
    private val repository: ClassroomRepository
) {
    suspend operator fun invoke(): ApiResult<PaginationResponse<ClassroomWrapperResponse>> {
        return repository.getClassroom()
    }
}