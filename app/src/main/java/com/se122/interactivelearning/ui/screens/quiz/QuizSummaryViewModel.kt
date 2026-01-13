package com.se122.interactivelearning.ui.screens.quiz

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.ProfileResponse
import com.se122.interactivelearning.domain.model.QuizLeaderboardEntry
import com.se122.interactivelearning.domain.repository.QuizSocketRepository
import com.se122.interactivelearning.domain.usecase.profile.GetProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizSummaryViewModel @Inject constructor(
    private val quizSocketRepository: QuizSocketRepository,
    private val getProfileUseCase: GetProfileUseCase
): ViewModel() {
    private val _leaderboard = MutableStateFlow<List<QuizLeaderboardEntry>>(emptyList())
    val leaderboard = _leaderboard.asStateFlow()

    private val _profile = MutableStateFlow<ProfileResponse?>(null)
    val profile = _profile.asStateFlow()

    init {
        viewModelScope.launch {
            when (val result = getProfileUseCase()) {
                is ApiResult.Success -> {
                    _profile.value = result.data
                }
                else -> {
                    Log.e("QuizSummaryViewModel", "getProfile error")
                }
            }
        }
    }

    fun getLeaderboard() {
        viewModelScope.launch {
            quizSocketRepository.onUpdateLeaderboard {
                _leaderboard.value = it
                Log.i("QuizSummaryViewModel", "getLeaderboard: $it")
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            quizSocketRepository.disconnect()
        }
    }
}