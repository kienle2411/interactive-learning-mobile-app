package com.se122.interactivelearning.data.repository

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.api.AssignmentApi
import com.se122.interactivelearning.data.remote.api.safeApiCall
import com.se122.interactivelearning.data.remote.dto.AssignmentQuestionsResponse
import com.se122.interactivelearning.data.remote.dto.SubmissionResponse
import com.se122.interactivelearning.data.remote.dto.SubmitAssignmentRequest
import com.se122.interactivelearning.domain.repository.AssignmentRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class AssignmentRepositoryImpl @Inject constructor(
    private val assignmentApi: AssignmentApi
): AssignmentRepository {
    override suspend fun getAssignmentQuestions(id: String): ApiResult<AssignmentQuestionsResponse> {
        return safeApiCall {
            assignmentApi.getAssignmentQuestions(id)
        }
    }

    override suspend fun submitAssignment(
        assignmentId: String,
        request: SubmitAssignmentRequest
    ): ApiResult<SubmissionResponse> {
        return safeApiCall {
            assignmentApi.submitAssignment(assignmentId, request)
        }
    }

    override suspend fun submitAssignmentWithFiles(
        assignmentId: String,
        files: List<MultipartBody.Part>
    ): ApiResult<SubmissionResponse> {
        return safeApiCall {
            assignmentApi.submitAssignmentWithFiles(assignmentId, files)
        }
    }

    override suspend fun getAssignmentSubmissions(id: String): ApiResult<List<SubmissionResponse>> {
        return safeApiCall {
            assignmentApi.getAssignmentSubmissions(id)
        }
    }
}
