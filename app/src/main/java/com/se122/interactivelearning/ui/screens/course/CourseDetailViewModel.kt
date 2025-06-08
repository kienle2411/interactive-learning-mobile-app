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
import com.se122.interactivelearning.data.remote.dto.MaterialResponse
import com.se122.interactivelearning.data.remote.dto.MeetingResponse
import com.se122.interactivelearning.data.remote.dto.SessionResponse
import com.se122.interactivelearning.domain.usecase.classroom.GetClassroomAssignmentsUseCase
import com.se122.interactivelearning.domain.usecase.classroom.GetClassroomDetailUseCase
import com.se122.interactivelearning.domain.usecase.classroom.GetClassroomMaterialsUseCase
import com.se122.interactivelearning.domain.usecase.classroom.GetClassroomMeetingUseCase
import com.se122.interactivelearning.domain.usecase.classroom.GetClassroomSessionsUseCase
import com.se122.interactivelearning.domain.usecase.classroom.GetClassroomStudentsUseCase
import com.se122.interactivelearning.domain.usecase.file.GetFileDownloadLinkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    private val getClassroomDetailUseCase: GetClassroomDetailUseCase,
    private val getClassroomStudentsUseCase: GetClassroomStudentsUseCase,
    private val getClassroomMaterialsUseCase: GetClassroomMaterialsUseCase,
    private val getFileDownloadLinkUseCase: GetFileDownloadLinkUseCase,
    private val getClassroomSessionsUseCase: GetClassroomSessionsUseCase,
    private val getClassroomMeetingUseCase: GetClassroomMeetingUseCase,
    private val getClassroomAssignmentsUseCase: GetClassroomAssignmentsUseCase,
): ViewModel() {
    private val _classroomDetails = MutableStateFlow<ViewState<ClassroomDetailsResponse>>(ViewState.Idle)
    val classroomDetails = _classroomDetails.asStateFlow()

    private val _classroomStudents = MutableStateFlow<ViewState<List<ClassroomStudentResponse>>>(ViewState.Idle)
    val classroomStudents = _classroomStudents.asStateFlow()

    private val _classroomMaterials = MutableStateFlow<ViewState<List<MaterialResponse>>>(ViewState.Idle)
    val classroomMaterials = _classroomMaterials.asStateFlow()

    private val _fileDownloadLink = MutableStateFlow<ViewState<String>>(ViewState.Idle)
    val fileDownloadLink = _fileDownloadLink.asStateFlow()

    private val _classroomSessions = MutableStateFlow<ViewState<List<SessionResponse>>>(ViewState.Idle)
    val classroomSessions = _classroomSessions.asStateFlow()

    private val _classroomMeetings = MutableStateFlow<ViewState<List<MeetingResponse>>>(ViewState.Idle)
    val classroomMeetings = _classroomMeetings.asStateFlow()

    private val _classroomAssignments = MutableStateFlow<ViewState<List<AssignmentResponse>>>(ViewState.Idle)
    val classroomAssignments = _classroomAssignments.asStateFlow()

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
                    _classroomSessions.value = ViewState.Success(result.data.data)
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

    fun loadMeetings(id: String) {
        viewModelScope.launch {
            _classroomMeetings.value = ViewState.Loading
            when (val result = getClassroomMeetingUseCase(id)) {
                is ApiResult.Success -> {
                    _classroomMeetings.value = ViewState.Success(result.data.data)
                }
                is ApiResult.Error -> {
                    Log.i("LoginViewModel", "Error: $result")
                    val msg = (result.message + " " + result.errors?.first())
                    _classroomMeetings.value = Error(msg)
                }
                is ApiResult.Exception -> {
                    val msg = "Unknown error"
                    _classroomMeetings.value = Error(msg)
                }
            }
        }
    }

    fun loadAssignments(id: String) {
        viewModelScope.launch {
            _classroomAssignments.value = ViewState.Loading
            when (val result = getClassroomAssignmentsUseCase(id)) {
                is ApiResult.Success -> {
                    _classroomAssignments.value = ViewState.Success(result.data.data)
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
}