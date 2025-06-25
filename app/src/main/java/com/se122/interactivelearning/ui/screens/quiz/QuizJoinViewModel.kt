package com.se122.interactivelearning.ui.screens.quiz

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.QuizResponse
import com.se122.interactivelearning.domain.repository.QuizSocketRepository
import com.se122.interactivelearning.domain.usecase.quiz.GetQuizInformationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizJoinViewModel @Inject constructor(
    private val getQuizInformationUseCase: GetQuizInformationUseCase,
    private val quizSocketRepository: QuizSocketRepository
): ViewModel() {
    private val _quiz = MutableStateFlow<ViewState<QuizResponse>>(ViewState.Idle)
    val quiz = _quiz.asStateFlow()

    private val _start = MutableStateFlow<Boolean>(false)
    val start = _start.asStateFlow()

    val startEvent = quizSocketRepository.startEvent

    fun connectSocket() {
        viewModelScope.launch {
            quizSocketRepository.connect {
                observeQuizStart()
            }
        }
    }

    fun observeQuizStart() {
        quizSocketRepository.onQuizStarted {
            _start.value = true
        }
    }

    fun joinQuiz(id: String) {
        quizSocketRepository.joinQuiz(id)
    }

    fun getQuiz(id: String) {
        viewModelScope.launch {
            _quiz.value = ViewState.Loading
            when (val result = getQuizInformationUseCase(id)) {
                is ApiResult.Success -> {
                    _quiz.value = ViewState.Success(result.data)
                }
                else -> {
                    _quiz.value = ViewState.Error("Error")
                }
            }
        }
    }
}