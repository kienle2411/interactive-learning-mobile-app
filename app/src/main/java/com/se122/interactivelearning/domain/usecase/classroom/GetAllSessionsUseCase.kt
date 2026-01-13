package com.se122.interactivelearning.domain.usecase.classroom

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.MeetingResponse
import com.se122.interactivelearning.data.remote.dto.SessionResponse
import com.se122.interactivelearning.domain.repository.ClassroomRepository
import java.time.OffsetDateTime
import java.time.ZoneOffset
import javax.inject.Inject

class GetAllSessionsUseCase @Inject constructor(
    private val repository: ClassroomRepository
) {
    suspend operator fun invoke(): List<SessionResponse> {
        val result = repository.getClassroom()
        if (result !is ApiResult.Success) {
            return emptyList()
        }

        val classrooms = result.data.data
        val allSessions = mutableListOf<SessionResponse>()

        val now = OffsetDateTime.now(ZoneOffset.UTC)

        for (classroomWrapper in classrooms) {
            val classroomId = classroomWrapper.classroom.id
            val sessionResult = repository.getClassroomSessions(classroomId)

            if (sessionResult is ApiResult.Success) {
                val validSessions = sessionResult.data.data.filter { session ->
                    session.deletedAt == null &&
                            try {
                                val endTime = OffsetDateTime.parse(session.endTime)
                                endTime.isAfter(now)
                            } catch (e: Exception) {
                                false
                            }
                }
                allSessions.addAll(validSessions)
            }
        }
        return allSessions
    }
}