package com.se122.interactivelearning.domain.usecase.classroom

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.MeetingResponse
import com.se122.interactivelearning.domain.repository.ClassroomRepository
import java.time.OffsetDateTime
import java.time.ZoneOffset
import javax.inject.Inject

class GetAllMeetingsUseCase @Inject constructor(
    private val repository: ClassroomRepository
) {
    suspend operator fun invoke(): List<MeetingResponse> {
        val result = repository.getClassroom()
        if (result !is ApiResult.Success) {
            return emptyList()
        }

        val classrooms = result.data.data
        val allMeetings = mutableListOf<MeetingResponse>()

        val now = OffsetDateTime.now(ZoneOffset.UTC)

        for (classroomWrapper in classrooms) {
            val classroomId = classroomWrapper.classroom.id
            val meetingResult = repository.getClassroomMeetings(classroomId)

            if (meetingResult is ApiResult.Success) {
                val validMeetings = meetingResult.data.data.filter { meeting ->
                    meeting.deletedAt == null &&
                            try {
                                val endTime = OffsetDateTime.parse(meeting.endTime)
                                endTime.isAfter(now)
                            } catch (e: Exception) {
                                false
                            }
                }
                allMeetings.addAll(validMeetings)
            }
        }
        return allMeetings
    }
}
