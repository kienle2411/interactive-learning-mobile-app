package com.se122.interactivelearning.ui.screens.lesson

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.common.ViewState.Error
import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.LessonDetailResponse
import com.se122.interactivelearning.data.remote.dto.QuizResponse
import com.se122.interactivelearning.domain.usecase.classroom.GetLessonDetailUseCase
import com.se122.interactivelearning.domain.usecase.classroom.GetLessonQuizzesUseCase
import com.se122.interactivelearning.domain.usecase.quiz.GetQuizAttemptsForStudentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonDetailViewModel @Inject constructor(
    private val getLessonDetailUseCase: GetLessonDetailUseCase,
    private val getLessonQuizzesUseCase: GetLessonQuizzesUseCase,
    private val getQuizAttemptsForStudentUseCase: GetQuizAttemptsForStudentUseCase,
) : ViewModel() {
    private val _lessonDetail = MutableStateFlow<ViewState<LessonDetailResponse>>(ViewState.Idle)
    val lessonDetail = _lessonDetail.asStateFlow()

    private val _lessonQuizzes = MutableStateFlow<ViewState<List<QuizResponse>>>(ViewState.Idle)
    val lessonQuizzes = _lessonQuizzes.asStateFlow()

    private val _quizCompletion = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val quizCompletion = _quizCompletion.asStateFlow()

    fun loadLesson(id: String) {
        viewModelScope.launch {
            _lessonDetail.value = ViewState.Loading
            when (val result = getLessonDetailUseCase(id)) {
                is ApiResult.Success -> {
                    _lessonDetail.value = ViewState.Success(result.data)
                }
                is ApiResult.Error -> {
                    Log.i("LessonDetailViewModel", "Lesson Error: $result")
                    val msg = (result.message + " " + result.errors?.first())
                    _lessonDetail.value = Error(msg)
                }
                is ApiResult.Exception -> {
                    val msg = "Unknown error"
                    _lessonDetail.value = Error(msg)
                }
            }
        }
    }

    fun loadQuizzes(id: String) {
        viewModelScope.launch {
            _lessonQuizzes.value = ViewState.Loading
            when (val result = getLessonQuizzesUseCase(id)) {
                is ApiResult.Success -> {
                    val filtered = result.data.filter { it.deletedAt == null }
                    _lessonQuizzes.value = ViewState.Success(filtered)
                    _quizCompletion.value = getQuizCompletion(filtered)
                }
                is ApiResult.Error -> {
                    Log.i("LessonDetailViewModel", "Quiz Error: $result")
                    val msg = (result.message + " " + result.errors?.first())
                    _lessonQuizzes.value = Error(msg)
                }
                is ApiResult.Exception -> {
                    val msg = "Unknown error"
                    _lessonQuizzes.value = Error(msg)
                }
            }
        }
    }

    private suspend fun getQuizCompletion(
        quizzes: List<QuizResponse>
    ): Map<String, Boolean> = coroutineScope {
        quizzes.map { quiz ->
            async {
                val isDone = when (val result = getQuizAttemptsForStudentUseCase(quiz.id)) {
                    is ApiResult.Success -> result.data.any { !it.endTime.isNullOrBlank() }
                    else -> false
                }
                quiz.id to isDone
            }
        }.awaitAll().toMap()
    }
}
