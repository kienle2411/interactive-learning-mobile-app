package com.se122.interactivelearning.ui.screens.quiz

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.QuestionResponse
import com.se122.interactivelearning.domain.repository.QuizSocketRepository
import com.se122.interactivelearning.domain.usecase.question.GetQuestionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InQuizViewModel @Inject constructor(
    private val quizSocketRepository: QuizSocketRepository,
    private val getQuestionUseCase: GetQuestionUseCase
): ViewModel() {
    private val _question = MutableStateFlow<ViewState<QuestionResponse>>(ViewState.Idle)
    val question = _question.asStateFlow()

    private val _countdownTime = MutableStateFlow(0)
    val countdownTime = _countdownTime.asStateFlow()

    private val _end = MutableStateFlow<Boolean>(false)
    val end = _end.asStateFlow()

    private var countdownJob: Job? = null

    fun observeQuestion() {
        viewModelScope.launch {
            quizSocketRepository.onReceiveQuestion {
                Log.i("InQuizViewModel", "observeQuestion: $it")
                getQuestion(it)
            }
        }
    }

    fun observeEndQuiz() {
        viewModelScope.launch {
            quizSocketRepository.onQuizEnded {
                _end.value = true
                Log.i("InQuizViewModel", "observeEndQuiz")
            }
        }
    }

    fun resetEnd() {
        viewModelScope.launch {
            _end.value = false
        }
    }

    fun submitAnswer(quizId: String, questionId: String, optionId: String) {
        viewModelScope.launch {
            quizSocketRepository.submitAnswer(quizId, questionId, optionId)
        }
    }

    fun getQuestion(questionId: String) {
        viewModelScope.launch {
            Log.i("InQuizViewModel", "getQuestion: $questionId")
            _question.value = ViewState.Loading
            when (val result = getQuestionUseCase(questionId)) {
                is ApiResult.Success -> {
                    val question = result.data
                    _question.value = ViewState.Success(question)
                    startCountdown(question.timeLimit)
                    Log.i("InQuizViewModel", "getQuestion")
                }
                else -> {
                    Log.i("InQuizViewModel", "Else")
                }
            }
        }
    }

    fun startCountdown(timeLimit: Int) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            _countdownTime.value = timeLimit
            while (_countdownTime.value > 0) {
                delay(1000)
                _countdownTime.value--
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            quizSocketRepository.disconnect()
        }
    }
}