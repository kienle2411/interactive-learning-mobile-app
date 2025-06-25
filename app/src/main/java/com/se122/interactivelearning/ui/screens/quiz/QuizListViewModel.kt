package com.se122.interactivelearning.ui.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.QuizAttemptResponse
import com.se122.interactivelearning.domain.usecase.quiz.AttemptQuizByCodeUseCase
import com.se122.interactivelearning.domain.usecase.quiz.AttemptQuizUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizListViewModel @Inject constructor(
    private val attemptQuizUseCase: AttemptQuizUseCase,
    private val attemptQuizByCodeUseCase: AttemptQuizByCodeUseCase
): ViewModel() {
    private val _attemptQuiz = MutableStateFlow<ViewState<QuizAttemptResponse>>(ViewState.Idle)
    val attemptQuiz = _attemptQuiz.asStateFlow()

    fun attemptQuiz(code: String) {
        viewModelScope.launch {
            _attemptQuiz.value = ViewState.Loading
            when (val result = attemptQuizByCodeUseCase(code)) {
                is ApiResult.Success -> {
                    _attemptQuiz.value = ViewState.Success(result.data)
                }
                else -> {}
            }
        }
    }

    fun resetAttemptQuiz() {
        viewModelScope.launch {
            _attemptQuiz.value = ViewState.Idle
        }
    }
}