package com.se122.interactivelearning.domain.repository

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.AssignmentQuestionsResponse
import com.se122.interactivelearning.data.remote.dto.SubmissionResponse
import com.se122.interactivelearning.data.remote.dto.SubmitAssignmentRequest
import okhttp3.MultipartBody

interface AssignmentRepository {
    suspend fun getAssignmentQuestions(id: String): ApiResult<AssignmentQuestionsResponse>
    suspend fun submitAssignment(
        assignmentId: String,
        request: SubmitAssignmentRequest
    ): ApiResult<SubmissionResponse>
    suspend fun submitAssignmentWithFiles(
        assignmentId: String,
        files: List<MultipartBody.Part>
    ): ApiResult<SubmissionResponse>
    suspend fun getAssignmentSubmissions(id: String): ApiResult<List<SubmissionResponse>>
}
