package com.se122.interactivelearning.ui.screens.course

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.common.ViewState.Error
import com.se122.interactivelearning.common.ViewState.Success
import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.ClassroomResponse
import com.se122.interactivelearning.data.remote.dto.ClassroomStudentResponse
import com.se122.interactivelearning.data.remote.dto.ClassroomWrapperResponse
import com.se122.interactivelearning.domain.usecase.GetClassroomListUseCase
import com.se122.interactivelearning.domain.usecase.JoinClassroomUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseViewModel @Inject constructor(
    private val getClassroomListUseCase: GetClassroomListUseCase,
    private val joinClassroomUseCase: JoinClassroomUseCase
): ViewModel() {
    private val _classroomList = MutableStateFlow<ViewState<List<ClassroomWrapperResponse>>>(ViewState.Idle)
    val classroomList = _classroomList.asStateFlow()

    private val _joinClassroomState = MutableStateFlow<ViewState<ClassroomStudentResponse>>(ViewState.Idle)
    val joinClassroomState = _joinClassroomState.asStateFlow()

    init {
        viewModelScope.launch {
            loadClassroomList()
        }
    }

    fun loadClassroomList() {
        viewModelScope.launch {
            _classroomList.value = ViewState.Loading
            when (val result = getClassroomListUseCase.invoke()) {
                is ApiResult.Success -> {
                    _classroomList.value = Success(result.data.data)
                }
                is ApiResult.Error -> {
                    Log.i("LoginViewModel", "Error: $result")
                    val msg = (result.message + "\n" + result.errors?.first())
                    _classroomList.value = Error(msg)
                }
                is ApiResult.Exception -> {
                    val msg = "Unknown error"
                    _classroomList.value = Error(msg)
                }
            }
        }
    }

    fun joinClassroom(code: String) {
        viewModelScope.launch {
            _joinClassroomState.value = ViewState.Loading
            when (val result = joinClassroomUseCase.invoke(code)) {
                is ApiResult.Success -> {
                    _joinClassroomState.value = Success(result.data)
                    loadClassroomList()
                }
                is ApiResult.Error -> {
                    Log.i("LoginViewModel", "Error: $result")
                    val msg = (result.message)
                    _joinClassroomState.value = Error(msg)
                }
                is ApiResult.Exception -> {
                    val msg = "Unknown error"
                    _joinClassroomState.value = Error(msg)
                }
            }
        }
    }
}
