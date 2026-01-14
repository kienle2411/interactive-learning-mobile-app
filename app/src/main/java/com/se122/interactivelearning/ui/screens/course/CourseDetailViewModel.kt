package com.se122.interactivelearning.ui.screens.course

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.common.ViewState.Error
import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.AssignmentResponse
import com.se122.interactivelearning.data.remote.dto.ClassroomDetailsResponse
import com.se122.interactivelearning.data.remote.dto.ClassroomStudentResponse
import com.se122.interactivelearning.data.remote.dto.LessonResponse
import com.se122.interactivelearning.data.remote.dto.MaterialResponse
import com.se122.interactivelearning.data.remote.dto.SessionResponse
import com.se122.interactivelearning.data.remote.dto.SubmissionStatus
import com.se122.interactivelearning.data.remote.dto.TopicResponse
import com.se122.interactivelearning.data.remote.dto.SuggestionsResponse
import com.se122.interactivelearning.domain.usecase.assignment.GetAssignmentSubmissionsUseCase
import com.se122.interactivelearning.domain.usecase.classroom.GetClassroomAssignmentsUseCase
import com.se122.interactivelearning.domain.usecase.classroom.GetClassroomDetailUseCase
import com.se122.interactivelearning.domain.usecase.classroom.GetClassroomMaterialsUseCase
import com.se122.interactivelearning.domain.usecase.classroom.GetClassroomAISuggestionsUseCase
import com.se122.interactivelearning.domain.usecase.classroom.GetClassroomSessionsUseCase
import com.se122.interactivelearning.domain.usecase.classroom.GetClassroomStudentsUseCase
import com.se122.interactivelearning.domain.usecase.classroom.GetClassroomTopicsUseCase
import com.se122.interactivelearning.domain.usecase.classroom.GetTopicLessonsUseCase
import com.se122.interactivelearning.domain.usecase.file.GetFileDownloadLinkUseCase
import com.se122.interactivelearning.ui.model.AssignmentUi
import com.se122.interactivelearning.ui.model.AssignmentStatus
import com.se122.interactivelearning.ui.model.SessionUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    private val getClassroomDetailUseCase: GetClassroomDetailUseCase,
    private val getClassroomStudentsUseCase: GetClassroomStudentsUseCase,
    private val getClassroomMaterialsUseCase: GetClassroomMaterialsUseCase,
    private val getFileDownloadLinkUseCase: GetFileDownloadLinkUseCase,
    private val getClassroomSessionsUseCase: GetClassroomSessionsUseCase,
    private val getClassroomAssignmentsUseCase: GetClassroomAssignmentsUseCase,
    private val getAssignmentSubmissionsUseCase: GetAssignmentSubmissionsUseCase,
    private val getClassroomAISuggestionsUseCase: GetClassroomAISuggestionsUseCase,
    private val getClassroomTopicsUseCase: GetClassroomTopicsUseCase,
    private val getTopicLessonsUseCase: GetTopicLessonsUseCase,
): ViewModel() {
    private val _classroomDetails = MutableStateFlow<ViewState<ClassroomDetailsResponse>>(ViewState.Idle)
    val classroomDetails = _classroomDetails.asStateFlow()

    private val _classroomStudents = MutableStateFlow<ViewState<List<ClassroomStudentResponse>>>(ViewState.Idle)
    val classroomStudents = _classroomStudents.asStateFlow()

    private val _classroomMaterials = MutableStateFlow<ViewState<List<MaterialResponse>>>(ViewState.Idle)
    val classroomMaterials = _classroomMaterials.asStateFlow()

    private val _fileDownloadLink = MutableStateFlow<ViewState<String>>(ViewState.Idle)
    val fileDownloadLink = _fileDownloadLink.asStateFlow()

    private val _classroomSessions = MutableStateFlow<ViewState<List<SessionUi>>>(ViewState.Idle)
    val classroomSessions = _classroomSessions.asStateFlow()

    private val _classroomAssignments = MutableStateFlow<ViewState<List<AssignmentUi>>>(ViewState.Idle)
    val classroomAssignments = _classroomAssignments.asStateFlow()

    private val _classroomTopics = MutableStateFlow<ViewState<List<TopicResponse>>>(ViewState.Idle)
    val classroomTopics = _classroomTopics.asStateFlow()

    private val _topicLessons = MutableStateFlow<Map<String, ViewState<List<LessonResponse>>>>(emptyMap())
    val topicLessons = _topicLessons.asStateFlow()

    private val _classroomSuggestions =
        MutableStateFlow<ViewState<SuggestionsResponse>>(ViewState.Idle)
    val classroomSuggestions = _classroomSuggestions.asStateFlow()

    init {

    }

    fun loadCourseDetails(id: String) {
        viewModelScope.launch {
            _classroomDetails.value = ViewState.Loading
            when (val result = getClassroomDetailUseCase(id)) {
                is ApiResult.Success -> {
                    _classroomDetails.value = ViewState.Success(result.data)
                }
                is ApiResult.Error -> {
                    Log.i("LoginViewModel", "Error: $result")
                    val msg = (result.message + "\n" + result.errors?.first())
                    _classroomDetails.value = Error(msg)
                }
                is ApiResult.Exception -> {
                    val msg = "Unknown error"
                    _classroomDetails.value = Error(msg)
                }
            }
        }
    }

    fun loadStudentList(id: String) {
        viewModelScope.launch {
            _classroomStudents.value = ViewState.Loading
            when (val result = getClassroomStudentsUseCase(id)) {
                is ApiResult.Success -> {
                    _classroomStudents.value = ViewState.Success(result.data.data)
                }
                is ApiResult.Error -> {
                    Log.i("LoginViewModel", "Error: $result")
                    val msg = (result.message + " " + result.errors?.first())
                    _classroomStudents.value = Error(msg)
                }
                is ApiResult.Exception -> {
                    val msg = "Unknown error"
                    _classroomStudents.value = Error(msg)
                }
            }
        }
    }

    fun loadMaterials(id: String) {
        viewModelScope.launch {
            _classroomMaterials.value = ViewState.Loading
            when (val result = getClassroomMaterialsUseCase(id)) {
                is ApiResult.Success -> {
                    _classroomMaterials.value = ViewState.Success(result.data.data)
                }
                is ApiResult.Error -> {
                    Log.i("LoginViewModel", "Error: $result")
                    val msg = (result.message + " " + result.errors?.first())
                    _classroomMaterials.value = Error(msg)
                }
                is ApiResult.Exception -> {
                    val msg = "Unknown error"
                    _classroomMaterials.value = Error(msg)
                }
            }
        }
    }

    fun loadSessions(id: String) {
        viewModelScope.launch {
            _classroomSessions.value = ViewState.Loading
            when (val result = getClassroomSessionsUseCase(id)) {
                is ApiResult.Success -> {
                    val items = withContext(Dispatchers.Default) {
                        mapSessions(result.data.data)
                    }
                    _classroomSessions.value = ViewState.Success(items)
                }
                is ApiResult.Error -> {
                    Log.i("LoginViewModel", "Error: $result")
                    val msg = (result.message + " " + result.errors?.first())
                    _classroomSessions.value = Error(msg)
                }
                is ApiResult.Exception -> {
                    val msg = "Unknown error"
                    _classroomSessions.value = Error(msg)
                }
            }
        }
    }

    fun loadAssignments(id: String) {
        viewModelScope.launch {
            _classroomAssignments.value = ViewState.Loading
            when (val result = getClassroomAssignmentsUseCase(id)) {
                is ApiResult.Success -> {
                    val submissionMap = getSubmissionStatus(result.data.data)
                    val items = withContext(Dispatchers.Default) {
                        mapAssignments(result.data.data, submissionMap)
                    }
                    _classroomAssignments.value = ViewState.Success(items)
                }
                is ApiResult.Error -> {
                    Log.i("LoginViewModel", "Error: $result")
                    val msg = (result.message + " " + result.errors?.first())
                    _classroomAssignments.value = Error(msg)
                }
                is ApiResult.Exception -> {
                    val msg = "Unknown error"
                    _classroomAssignments.value = Error(msg)
                }
            }
        }
    }

    fun getFileDownloadLink(id: String) {
        viewModelScope.launch {
            Log.i("VM", "Get download")
            _fileDownloadLink.value = ViewState.Loading
            when (val result = getFileDownloadLinkUseCase(id)) {
                is ApiResult.Success -> {
                    _fileDownloadLink.value = ViewState.Success(result.data)
                    Log.i("Intent", "get get")
                    Log.i("Intent", result.data)
                }
                is ApiResult.Error -> {
                    Log.i("LoginViewModel", "Error: $result")
                    val msg = (result.message + " " + result.errors?.first())
                    _fileDownloadLink.value = Error(msg)
                }
                is ApiResult.Exception -> {

                    val msg = "Unknown error"
                    Log.i("VM", result.toString())
                    _fileDownloadLink.value = Error(msg)
                }
            }
        }
    }

    fun loadTopics(id: String) {
        viewModelScope.launch {
            _classroomTopics.value = ViewState.Loading
            when (val result = getClassroomTopicsUseCase(id)) {
                is ApiResult.Success -> {
                    val filtered = result.data.data.filter { it.deletedAt == null }
                    _classroomTopics.value = ViewState.Success(filtered)
                }
                is ApiResult.Error -> {
                    Log.i("CourseDetailViewModel", "Topics Error: $result")
                    val msg = (result.message + " " + result.errors?.first())
                    _classroomTopics.value = Error(msg)
                }
                is ApiResult.Exception -> {
                    val msg = "Unknown error"
                    _classroomTopics.value = Error(msg)
                }
            }
        }
    }

    fun loadClassroomSuggestions(id: String) {
        viewModelScope.launch {
            _classroomSuggestions.value = ViewState.Loading
            when (val result = getClassroomAISuggestionsUseCase(id)) {
                is ApiResult.Success -> _classroomSuggestions.value = ViewState.Success(result.data)
                is ApiResult.Error -> _classroomSuggestions.value = Error(result.message)
                is ApiResult.Exception -> _classroomSuggestions.value = Error(result.e.message)
            }
        }
    }

    fun loadLessonsForTopic(topicId: String) {
        viewModelScope.launch {
            _topicLessons.value = _topicLessons.value + (topicId to ViewState.Loading)
            when (val result = getTopicLessonsUseCase(topicId)) {
                is ApiResult.Success -> {
                    val filtered = result.data.data.filter { it.deletedAt == null }
                    _topicLessons.value = _topicLessons.value + (topicId to ViewState.Success(filtered))
                }
                is ApiResult.Error -> {
                    Log.i("CourseDetailViewModel", "Lessons Error: $result")
                    val msg = (result.message + " " + result.errors?.first())
                    _topicLessons.value = _topicLessons.value + (topicId to ViewState.Error(msg))
                }
                is ApiResult.Exception -> {
                    val msg = "Unknown error"
                    _topicLessons.value = _topicLessons.value + (topicId to ViewState.Error(msg))
                }
            }
        }
    }

    private fun mapSessions(items: List<SessionResponse>): List<SessionUi> {
        val formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")
        val now = ZonedDateTime.now()
        return items.map { session ->
            val start = ZonedDateTime.parse(session.startTime, DateTimeFormatter.ISO_DATE_TIME)
            val end = ZonedDateTime.parse(session.endTime, DateTimeFormatter.ISO_DATE_TIME)
            SessionUi(
                id = session.id,
                title = session.title,
                description = session.description,
                startText = formatter.format(start),
                endText = formatter.format(end),
                canJoin = now.isAfter(start) && now.isBefore(end),
            )
        }
    }

    private fun mapAssignments(
        items: List<AssignmentResponse>,
        submissionMap: Map<String, Boolean>
    ): List<AssignmentUi> {
        val now = ZonedDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")
        return items.map { assignment ->
            val start = ZonedDateTime.parse(assignment.startTime, DateTimeFormatter.ISO_DATE_TIME)
            val due = ZonedDateTime.parse(assignment.dueTime, DateTimeFormatter.ISO_DATE_TIME)
            val isOngoing = now.isAfter(start) && now.isBefore(due)
            val isSubmitted = submissionMap[assignment.id] ?: assignment.submission?.any {
                it.status == SubmissionStatus.SUBMITTED || it.status == SubmissionStatus.GRADED
            } == true
            val status = if (isOngoing) {
                AssignmentStatus.ONGOING
            } else if (due.isBefore(now)) {
                AssignmentStatus.ENDED
            } else {
                AssignmentStatus.UPCOMING
            }
            val statusText = when (status) {
                AssignmentStatus.ONGOING -> "Ongoing"
                AssignmentStatus.ENDED -> "Ended"
                AssignmentStatus.UPCOMING -> "Upcoming"
            }
            AssignmentUi(
                id = assignment.id,
                title = assignment.title,
                description = assignment.description,
                statusText = statusText,
                status = status,
                isOngoing = isOngoing,
                isSubmitted = isSubmitted,
                startText = formatter.format(start),
                dueText = formatter.format(due),
                type = assignment.type,
            )
        }
    }

    private suspend fun getSubmissionStatus(
        assignments: List<AssignmentResponse>
    ): Map<String, Boolean> = coroutineScope {
        assignments.map { assignment ->
            async {
                val submitted = when (val result = getAssignmentSubmissionsUseCase(assignment.id)) {
                    is ApiResult.Success -> result.data.any {
                        it.status == SubmissionStatus.SUBMITTED || it.status == SubmissionStatus.GRADED
                    }
                    else -> false
                }
                assignment.id to submitted
            }
        }.awaitAll().toMap()
    }
}
