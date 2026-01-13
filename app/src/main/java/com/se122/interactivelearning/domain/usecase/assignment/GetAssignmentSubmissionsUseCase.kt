package com.se122.interactivelearning.domain.usecase.assignment

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.SubmissionResponse
import com.se122.interactivelearning.domain.repository.AssignmentRepository
import javax.inject.Inject

class GetAssignmentSubmissionsUseCase @Inject constructor(
    private val assignmentRepository: AssignmentRepository
) {
    suspend operator fun invoke(id: String): ApiResult<List<SubmissionResponse>> {
        return assignmentRepository.getAssignmentSubmissions(id)
    }
}
