package com.se122.interactivelearning.data.remote.api

import com.se122.interactivelearning.data.remote.dto.AssignmentResponse
import com.se122.interactivelearning.data.remote.dto.ClassroomDetailsResponse
import com.se122.interactivelearning.data.remote.dto.ClassroomStudentResponse
import com.se122.interactivelearning.data.remote.dto.ClassroomWrapperResponse
import com.se122.interactivelearning.data.remote.dto.Group
import com.se122.interactivelearning.data.remote.dto.LessonDetailResponse
import com.se122.interactivelearning.data.remote.dto.LessonResponse
import com.se122.interactivelearning.data.remote.dto.MaterialResponse
import com.se122.interactivelearning.data.remote.dto.MeetingResponse
import com.se122.interactivelearning.data.remote.dto.QuizResponse
import com.se122.interactivelearning.data.remote.dto.SessionResponse
import com.se122.interactivelearning.data.remote.dto.SuggestionsResponse
import com.se122.interactivelearning.data.remote.dto.TopicResponse
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

    @GET("classrooms/{id}/meetings")
    suspend fun getClassroomMeetings(@Path("id") id: String): Response<ApiResponse<PaginationResponse<MeetingResponse>>>

    @GET("classrooms/{id}/groups")
    suspend fun getClassroomGroups(@Path("id") classroomId: String): Response<ApiResponse<PaginationResponse<Group>>>

    @GET("classrooms/{id}/assignments")
    suspend fun getClassroomAssignments(@Path("id") id: String): Response<ApiResponse<PaginationResponse<AssignmentResponse>>>

    @GET("classrooms/{id}/topics")
    suspend fun getClassroomTopics(@Path("id") id: String): Response<ApiResponse<PaginationResponse<TopicResponse>>>

    @GET("classrooms/{id}/ai-suggestions/me")
    suspend fun getClassroomAISuggestions(@Path("id") id: String): Response<ApiResponse<SuggestionsResponse>>

    @GET("classrooms/ai-suggestions/overall/me")
    suspend fun getOverallAISuggestions(): Response<ApiResponse<SuggestionsResponse>>

    @GET("topics/{id}/lessons")
    suspend fun getTopicLessons(@Path("id") id: String): Response<ApiResponse<PaginationResponse<LessonResponse>>>

    @GET("lessons/{id}")
    suspend fun getLessonDetail(@Path("id") id: String): Response<ApiResponse<LessonDetailResponse>>

    @GET("lessons/{id}/quizzes")
    suspend fun getLessonQuizzes(@Path("id") id: String): Response<ApiResponse<List<QuizResponse>>>

    @POST("classrooms/join")
    suspend fun joinClassroom(@Query("code") code: String): Response<ApiResponse<ClassroomStudentResponse>>
}
