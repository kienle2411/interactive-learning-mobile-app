package com.se122.interactivelearning.domain.usecase

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.ClassroomStudentResponse
import com.se122.interactivelearning.domain.repository.ClassroomRepository
import javax.inject.Inject

class JoinClassroomUseCase @Inject constructor(
    private val classroomRepository: ClassroomRepository
) {
    suspend operator fun invoke(code: String): ApiResult<ClassroomStudentResponse> {
        return classroomRepository.joinClassroom(code)
    }

}