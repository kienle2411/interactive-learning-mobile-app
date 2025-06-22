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
import com.se122.interactivelearning.domain.usecase.profile.GetProfileUseCase
import com.se122.interactivelearning.domain.usecase.auth.LoginUseCase
import com.se122.interactivelearning.domain.usecase.classroom.GetAllMeetingsUseCase
import com.se122.interactivelearning.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val tokenManager: TokenManager,
    private val getAllMeetingsUseCase: GetAllMeetingsUseCase
): ViewModel() {
    val userState = mutableStateOf<ProfileResponse?>(null)
    val meetingsState = mutableStateOf<List<MeetingResponse>>(emptyList())

    fun loadUserProfile() {
        viewModelScope.launch {
            Log.i("HomeViewModel", "loadUserProfile is called")
            val result = getProfileUseCase.invoke()
            Log.i("HomeViewModel", "Result: $result")
            Log.i("HomeViewModel", "Result:")
            when(result) {
                is ApiResult.Success -> {
                    userState.value = result.data
                    Log.i("HomeViewModel", "Success: ${result.data}")
                    loadAllMeetings()
                }
                is ApiResult.Error -> {
                    Log.i("HomeViewModel", "Error: $result")
                    userState.value = null
                }
                is ApiResult.Exception -> {

                }
            }
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
            val meetings = getAllMeetingsUseCase()
            meetingsState.value = meetings
            Log.i("HomeViewModel", "Tổng số buổi học: ${meetings.size}")
            meetings.forEachIndexed { index, meeting ->
                Log.i("HomeViewModel", "Meeting $index: $meeting")
            }
        }
    }
}
