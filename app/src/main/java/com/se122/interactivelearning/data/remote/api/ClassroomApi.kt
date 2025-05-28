package com.se122.interactivelearning.data.remote.api

import com.se122.interactivelearning.data.remote.dto.AssignmentResponse
import com.se122.interactivelearning.data.remote.dto.ClassroomDetailsResponse
import com.se122.interactivelearning.data.remote.dto.ClassroomResponse
import com.se122.interactivelearning.data.remote.dto.ClassroomStudentResponse
import com.se122.interactivelearning.data.remote.dto.ClassroomWrapperResponse
import com.se122.interactivelearning.data.remote.dto.MaterialResponse
import com.se122.interactivelearning.data.remote.dto.MeetingResponse
import com.se122.interactivelearning.data.remote.dto.SessionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ClassroomApi {
    @GET("students/classrooms")
    suspend fun getClassroom(): Response<ApiResponse<PaginationResponse<ClassroomWrapperResponse>>>

    @GET("classrooms/{id}")
    suspend fun getClassroomDetails(@Path("id") id: String): Response<ApiResponse<ClassroomDetailsResponse>>

    @GET("classrooms/{id}/students")
    suspend fun getClassroomStudents(@Path("id") id: String): Response<ApiResponse<PaginationResponse<ClassroomStudentResponse>>>

    @GET("classrooms/{id}/sessions")
    suspend fun getClassroomSessions(@Path("id") id: String): Response<ApiResponse<PaginationResponse<SessionResponse>>>

    @GET("classrooms/{id}/materials")
    suspend fun getClassroomMaterials(@Path("id") id: String): Response<ApiResponse<PaginationResponse<MaterialResponse>>>

    @GET("classroom/{id}/meetings")
    suspend fun getClassroomMeetings(@Path("id") id: String): Response<ApiResponse<PaginationResponse<MeetingResponse>>>

    @GET("classrooms/{id}/assignments")
    suspend fun getClassroomAssignments(@Path("id") id: String): Response<ApiResponse<PaginationResponse<AssignmentResponse>>>

    @POST("classrooms/join")
    suspend fun joinClassroom(@Query("code") code: String): Response<ApiResponse<ClassroomStudentResponse>>
}