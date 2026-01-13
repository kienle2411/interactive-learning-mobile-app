package com.se122.interactivelearning.domain.usecase.classroom

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.api.PaginationResponse
import com.se122.interactivelearning.data.remote.dto.ClassroomWrapperResponse
import com.se122.interactivelearning.data.remote.dto.Group
import com.se122.interactivelearning.domain.repository.ClassroomRepository
import javax.inject.Inject

class GetClassroomGroupsUseCase @Inject constructor(
    private val repository: ClassroomRepository
) {
    suspend operator fun invoke(id: String): ApiResult<PaginationResponse<Group>> {
        return repository.getClassroomGroups(id)
    }
}