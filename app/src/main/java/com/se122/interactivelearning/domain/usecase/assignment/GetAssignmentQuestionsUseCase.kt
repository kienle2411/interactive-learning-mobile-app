package com.se122.interactivelearning.domain.usecase.assignment

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.AssignmentQuestionsResponse
import com.se122.interactivelearning.domain.repository.AssignmentRepository
import javax.inject.Inject

class GetAssignmentQuestionsUseCase @Inject constructor(
    private val assignmentRepository: AssignmentRepository
) {
    suspend operator fun invoke(id: String): ApiResult<AssignmentQuestionsResponse> {
        return assignmentRepository.getAssignmentQuestions(id)
    }
}
