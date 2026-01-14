package com.se122.interactivelearning.data.repository

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.api.ClassroomApi
import com.se122.interactivelearning.data.remote.api.PaginationResponse
import com.se122.interactivelearning.data.remote.api.safeApiCall
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
import com.se122.interactivelearning.domain.repository.ClassroomRepository
import javax.inject.Inject

class ClassroomRepositoryImpl @Inject constructor(
    private val classroomApi: ClassroomApi
): ClassroomRepository {
    override suspend fun getClassroom(): ApiResult<PaginationResponse<ClassroomWrapperResponse>> {
        return safeApiCall {
            classroomApi.getClassroom()
        }
    }
    override suspend fun getClassroomDetails(id: String): ApiResult<ClassroomDetailsResponse> {
        return safeApiCall {
            classroomApi.getClassroomDetails(id)
        }
    }
    override suspend fun getClassroomStudents(id: String): ApiResult<PaginationResponse<ClassroomStudentResponse>> {
        return safeApiCall {
            classroomApi.getClassroomStudents(id)
        }
    }
    override suspend fun getClassroomMaterials(id: String): ApiResult<PaginationResponse<MaterialResponse>> {
        return safeApiCall {
            classroomApi.getClassroomMaterials(id)
        }
    }
    override suspend fun getClassroomSessions(id: String): ApiResult<PaginationResponse<SessionResponse>> {
        return safeApiCall {
            classroomApi.getClassroomSessions(id)
        }
    }
    override suspend fun getClassroomMeetings(id: String): ApiResult<PaginationResponse<MeetingResponse>> {
        return safeApiCall {
            classroomApi.getClassroomMeetings(id)
        }
    }
    override suspend fun getClassroomAssignments(id: String): ApiResult<PaginationResponse<AssignmentResponse>> {
        return safeApiCall {
            classroomApi.getClassroomAssignments(id)
        }
    }

    override suspend fun getClassroomTopics(id: String): ApiResult<PaginationResponse<TopicResponse>> {
        return safeApiCall {
            classroomApi.getClassroomTopics(id)
        }
    }

    override suspend fun getClassroomAISuggestions(id: String): ApiResult<SuggestionsResponse> {
        return safeApiCall {
            classroomApi.getClassroomAISuggestions(id)
        }
    }

    override suspend fun getOverallAISuggestions(): ApiResult<SuggestionsResponse> {
        return safeApiCall {
            classroomApi.getOverallAISuggestions()
        }
    }

    override suspend fun getTopicLessons(id: String): ApiResult<PaginationResponse<LessonResponse>> {
        return safeApiCall {
            classroomApi.getTopicLessons(id)
        }
    }

    override suspend fun getLessonDetail(id: String): ApiResult<LessonDetailResponse> {
        return safeApiCall {
            classroomApi.getLessonDetail(id)
        }
    }

    override suspend fun getLessonQuizzes(id: String): ApiResult<List<QuizResponse>> {
        return safeApiCall {
            classroomApi.getLessonQuizzes(id)
        }
    }
    override suspend fun joinClassroom(code: String): ApiResult<ClassroomStudentResponse> {
        return safeApiCall {
            classroomApi.joinClassroom(code)
        }
    }

    override suspend fun getClassroomGroups(id: String): ApiResult<PaginationResponse<Group>> {
        return safeApiCall {
            classroomApi.getClassroomGroups(id)
        }
    }
}
