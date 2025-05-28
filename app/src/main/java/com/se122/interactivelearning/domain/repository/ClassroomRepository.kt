package com.se122.interactivelearning.domain.repository

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.api.PaginationResponse
import com.se122.interactivelearning.data.remote.dto.AssignmentResponse
import com.se122.interactivelearning.data.remote.dto.ClassroomDetailsResponse
import com.se122.interactivelearning.data.remote.dto.ClassroomResponse
import com.se122.interactivelearning.data.remote.dto.ClassroomStudentResponse
import com.se122.interactivelearning.data.remote.dto.ClassroomWrapperResponse
import com.se122.interactivelearning.data.remote.dto.MaterialResponse
import com.se122.interactivelearning.data.remote.dto.MeetingResponse
import com.se122.interactivelearning.data.remote.dto.SessionResponse

interface ClassroomRepository {
    suspend fun getClassroom(): ApiResult<PaginationResponse<ClassroomWrapperResponse>>
    suspend fun getClassroomDetails(id: String): ApiResult<ClassroomDetailsResponse>
    suspend fun getClassroomStudents(id: String): ApiResult<PaginationResponse<ClassroomStudentResponse>>
    suspend fun getClassroomMaterials(id: String): ApiResult<PaginationResponse<MaterialResponse>>
    suspend fun getClassroomSessions(id: String): ApiResult<PaginationResponse<SessionResponse>>
    suspend fun getClassroomMeetings(id: String): ApiResult<PaginationResponse<MeetingResponse>>
    suspend fun getClassroomAssignments(id: String): ApiResult<PaginationResponse<AssignmentResponse>>
    suspend fun joinClassroom(code: String): ApiResult<ClassroomStudentResponse>
}