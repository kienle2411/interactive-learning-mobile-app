package com.se122.interactivelearning.domain.usecase.assignment

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.SubmissionResponse
import com.se122.interactivelearning.domain.repository.AssignmentRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class SubmitAssignmentFilesUseCase @Inject constructor(
    private val assignmentRepository: AssignmentRepository
) {
    suspend operator fun invoke(
        assignmentId: String,
        files: List<MultipartBody.Part>
    ): ApiResult<SubmissionResponse> {
        return assignmentRepository.submitAssignmentWithFiles(assignmentId, files)
    }
}
