package com.se122.interactivelearning.ui.screens.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se122.interactivelearning.data.remote.api.ApiResponse
import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.MeetingResponse
import com.se122.interactivelearning.data.remote.dto.ProfileResponse
import com.se122.interactivelearning.data.remote.dto.SessionResponse
import com.se122.interactivelearning.data.remote.dto.SuggestionsResponse
import com.se122.interactivelearning.data.remote.dto.ClassroomWrapperResponse
import com.se122.interactivelearning.domain.usecase.profile.GetProfileUseCase
import com.se122.interactivelearning.domain.usecase.auth.LoginUseCase
import com.se122.interactivelearning.domain.usecase.classroom.GetAllMeetingsUseCase
import com.se122.interactivelearning.domain.usecase.classroom.GetAllSessionsUseCase
import com.se122.interactivelearning.domain.usecase.classroom.GetClassroomAISuggestionsUseCase
import com.se122.interactivelearning.domain.usecase.classroom.GetClassroomListUseCase
import com.se122.interactivelearning.domain.usecase.classroom.GetOverallAISuggestionsUseCase
import com.se122.interactivelearning.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val tokenManager: TokenManager,
    private val getAllMeetingsUseCase: GetAllMeetingsUseCase,
    private val getAllSessionsUseCase: GetAllSessionsUseCase,
    private val getClassroomListUseCase: GetClassroomListUseCase,
    private val getClassroomAISuggestionsUseCase: GetClassroomAISuggestionsUseCase,
    private val getOverallAISuggestionsUseCase: GetOverallAISuggestionsUseCase,
): ViewModel() {
    val userState = mutableStateOf<ProfileResponse?>(null)
    val meetingsState = mutableStateOf<List<MeetingResponse>>(emptyList())
    val sessionsState = mutableStateOf<List<SessionResponse>>(emptyList())
    val classroomsState = mutableStateOf<List<ClassroomWrapperResponse>>(emptyList())
    val overallSuggestionsState = mutableStateOf<SuggestionsResponse?>(null)
    val classroomSuggestionsState = mutableStateOf<Map<String, SuggestionsResponse>>(emptyMap())
    val isLoadingProfile = mutableStateOf(false)
    val isLoadingMeetings = mutableStateOf(false)
    val isLoadingSessions = mutableStateOf(false)
    val isLoadingClassrooms = mutableStateOf(false)
    val isLoadingSuggestions = mutableStateOf(false)

    fun loadUserProfile() {
        viewModelScope.launch {
            Log.i("HomeViewModel", "loadUserProfile is called")
            isLoadingProfile.value = true
            val result = getProfileUseCase.invoke()
            Log.i("HomeViewModel", "Result: $result")
            Log.i("HomeViewModel", "Result:")
            when(result) {
                is ApiResult.Success -> {
                    userState.value = result.data
                    Log.i("HomeViewModel", "Success: ${result.data}")
                    loadAllMeetings()
                    loadAllSessions()
                    loadClassroomsAndSuggestions()
                }
                is ApiResult.Error -> {
                    Log.i("HomeViewModel", "Error: $result")
                    userState.value = null
                }
                is ApiResult.Exception -> {

                }
            }
            isLoadingProfile.value = false
        }
    }

    fun clearToken() {
        viewModelScope.launch {
            tokenManager.clearToken()
            loadUserProfile()
        }
    }

    fun loadAllMeetings() {
        viewModelScope.launch {
            isLoadingMeetings.value = true
            val meetings = getAllMeetingsUseCase()
            meetingsState.value = meetings
            Log.i("HomeViewModel", "Tổng số buổi học: ${meetings.size}")
            meetings.forEachIndexed { index, meeting ->
                Log.i("HomeViewModel", "Meeting $index: $meeting")
            }
            isLoadingMeetings.value = false
        }
    }

    fun loadAllSessions() {
        viewModelScope.launch {
            isLoadingSessions.value = true
            val sessions = getAllSessionsUseCase()
            Log.i("HomeViewModel", "Số sessions trả về từ use case: ${sessions.size}") // <--- THÊM LOG

            sessionsState.value = sessions
            Log.i("HomeViewModel", "Tổng số session: ${sessions.size}")
            sessions.forEachIndexed { index, session ->
                Log.i("HomeViewModel", "Session $index: $session")
            }
            isLoadingSessions.value = false
        }
    }

    fun loadClassroomsAndSuggestions() {
        viewModelScope.launch {
            isLoadingClassrooms.value = true
            isLoadingSuggestions.value = true
            val result = getClassroomListUseCase()
            if (result !is ApiResult.Success) {
                classroomsState.value = emptyList()
                overallSuggestionsState.value = null
                classroomSuggestionsState.value = emptyMap()
                isLoadingClassrooms.value = false
                isLoadingSuggestions.value = false
                return@launch
            }

            val classrooms = result.data.data
            classroomsState.value = classrooms
            isLoadingClassrooms.value = false

            val overallResult = getOverallAISuggestionsUseCase()
            overallSuggestionsState.value =
                (overallResult as? ApiResult.Success)?.data

            val suggestionsMap = mutableMapOf<String, SuggestionsResponse>()
            for (classroom in classrooms) {
                val classroomId = classroom.classroom.id
                val suggestionResult = getClassroomAISuggestionsUseCase(classroomId)
                val data = (suggestionResult as? ApiResult.Success)?.data
                if (data != null) {
                    suggestionsMap[classroomId] = data
                }
            }

            classroomSuggestionsState.value = suggestionsMap
            isLoadingSuggestions.value = false
        }
    }
}
