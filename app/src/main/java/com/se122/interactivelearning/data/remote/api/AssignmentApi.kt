package com.se122.interactivelearning.data.remote.api

import com.se122.interactivelearning.data.remote.dto.AssignmentQuestionsResponse
import com.se122.interactivelearning.data.remote.dto.SubmissionResponse
import com.se122.interactivelearning.data.remote.dto.SubmitAssignmentRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface AssignmentApi {
    @GET("assignments/{id}/questions")
    suspend fun getAssignmentQuestions(
        @Path("id") id: String
    ): Response<ApiResponse<AssignmentQuestionsResponse>>

    @POST("assignments/{assignmentId}/submissions")
    suspend fun submitAssignment(
        @Path("assignmentId") assignmentId: String,
        @Body request: SubmitAssignmentRequest
    ): Response<ApiResponse<SubmissionResponse>>

    @Multipart
    @POST("assignments/{assignmentId}/submissions")
    suspend fun submitAssignmentWithFiles(
        @Path("assignmentId") assignmentId: String,
        @Part files: List<MultipartBody.Part>
    ): Response<ApiResponse<SubmissionResponse>>

    @GET("assignments/{id}/submissions")
    suspend fun getAssignmentSubmissions(
        @Path("id") id: String
    ): Response<ApiResponse<List<SubmissionResponse>>>
}
