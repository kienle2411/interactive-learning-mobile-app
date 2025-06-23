package com.se122.interactivelearning.ui.screens.quiz

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.QuestionResponse
import com.se122.interactivelearning.domain.repository.QuizSocketRepository
import com.se122.interactivelearning.domain.usecase.question.GetQuestionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InQuizViewModel @Inject constructor(
    private val quizSocketRepository: QuizSocketRepository,
    private val getQuestionUseCase: GetQuestionUseCase
): ViewModel() {
    private val _question = MutableStateFlow<QuestionResponse?>(null)
    val question = _question.asStateFlow()

    fun connectSocket() {
        quizSocketRepository.connect {
            observeQuestion()
        }
    }

    fun joinQuiz(quizId: String) {
        quizSocketRepository.joinQuiz(quizId)
    }

    fun observeQuestion() {
        quizSocketRepository.onReceiveQuestion {
            getQuestion(it)
        }
    }

    fun getQuestion(questionId: String) {
        viewModelScope.launch {
            when (val result = getQuestionUseCase(questionId)) {
                is ApiResult.Success -> {
                    _question.value = result.data
                }
                else -> {}
            }
        }
    }
}