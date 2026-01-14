package com.se122.interactivelearning.ui.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.ExplainQuestionResponse
import com.se122.interactivelearning.data.remote.dto.QuizAttemptQuestionItem
import com.se122.interactivelearning.data.remote.dto.QuizAttemptResponse
import com.se122.interactivelearning.data.remote.dto.QuizResponse
import com.se122.interactivelearning.data.remote.dto.ExplainQuestionRequest
import com.se122.interactivelearning.data.remote.dto.SubmitQuizAnswerRequest
import com.se122.interactivelearning.domain.usecase.question.ExplainQuestionUseCase
import com.se122.interactivelearning.domain.usecase.quiz.GetMyQuizAttemptUseCase
import com.se122.interactivelearning.domain.usecase.quiz.GetQuizAttemptsForStudentUseCase
import com.se122.interactivelearning.domain.usecase.quiz.GetQuizAttemptQuestionsUseCase
import com.se122.interactivelearning.domain.usecase.quiz.GetQuizInformationUseCase
import com.se122.interactivelearning.domain.usecase.quiz.SubmitQuizAnswerUseCase
import com.se122.interactivelearning.domain.usecase.quiz.SubmitQuizAttemptUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizAttemptViewModel @Inject constructor(
    private val getQuizInformationUseCase: GetQuizInformationUseCase,
    private val getMyQuizAttemptUseCase: GetMyQuizAttemptUseCase,
    private val getQuizAttemptsForStudentUseCase: GetQuizAttemptsForStudentUseCase,
    private val getQuizAttemptQuestionsUseCase: GetQuizAttemptQuestionsUseCase,
    private val submitQuizAnswerUseCase: SubmitQuizAnswerUseCase,
    private val submitQuizAttemptUseCase: SubmitQuizAttemptUseCase,
    private val explainQuestionUseCase: ExplainQuestionUseCase
) : ViewModel() {
    private val _quiz = MutableStateFlow<ViewState<QuizResponse>>(ViewState.Idle)
    val quiz = _quiz.asStateFlow()

    private val _attempt = MutableStateFlow<ViewState<QuizAttemptResponse>>(ViewState.Idle)
    val attempt = _attempt.asStateFlow()

    private val _questions = MutableStateFlow<ViewState<List<QuizAttemptQuestionItem>>>(ViewState.Idle)
    val questions = _questions.asStateFlow()

    private val _answers = MutableStateFlow<Map<String, QuizAnswerDraft>>(emptyMap())
    val answers = _answers.asStateFlow()

    private val _isReadOnly = MutableStateFlow(false)
    val isReadOnly = _isReadOnly.asStateFlow()

    private val _explanations =
        MutableStateFlow<Map<String, ViewState<ExplainQuestionResponse>>>(emptyMap())
    val explanations = _explanations.asStateFlow()

    private val _submitState = MutableStateFlow<ViewState<QuizAttemptResponse>>(ViewState.Idle)
    val submitState = _submitState.asStateFlow()

    fun loadQuiz(quizId: String) {
        viewModelScope.launch {
            _quiz.value = ViewState.Loading
            when (val result = getQuizInformationUseCase(quizId)) {
                is ApiResult.Success -> _quiz.value = ViewState.Success(result.data)
                is ApiResult.Error -> _quiz.value = ViewState.Error(result.message)
                is ApiResult.Exception -> _quiz.value = ViewState.Error(result.e.message)
            }
        }
    }

    fun loadAttempt(quizId: String) {
        viewModelScope.launch {
            _attempt.value = ViewState.Loading
            val finishedAttempt = getFinishedAttempt(quizId)
            if (finishedAttempt != null) {
                _attempt.value = ViewState.Success(finishedAttempt)
                _isReadOnly.value = true
                loadQuestions(finishedAttempt.id)
                return@launch
            }
            when (val result = getMyQuizAttemptUseCase(quizId)) {
                is ApiResult.Success -> {
                    _attempt.value = ViewState.Success(result.data)
                    _isReadOnly.value = false
                    loadQuestions(result.data.id)
                }
                is ApiResult.Error -> {
                    _attempt.value = ViewState.Error(result.message)
                    _questions.value = ViewState.Error(result.message)
                }
                is ApiResult.Exception -> {
                    _attempt.value = ViewState.Error(result.e.message)
                    _questions.value = ViewState.Error(result.e.message)
                }
            }
        }
    }

    private fun loadQuestions(attemptId: String) {
        viewModelScope.launch {
            _questions.value = ViewState.Loading
            when (val result = getQuizAttemptQuestionsUseCase(attemptId)) {
                is ApiResult.Success -> {
                    _questions.value = ViewState.Success(result.data)
                    seedAnswers(result.data)
                }
                is ApiResult.Error -> _questions.value = ViewState.Error(result.message)
                is ApiResult.Exception -> _questions.value = ViewState.Error(result.e.message)
            }
        }
    }

    fun setOptionAnswer(questionId: String, optionId: String) {
        _answers.update { current ->
            val existing = current[questionId]
            val updated = (existing ?: QuizAnswerDraft()).copy(
                selectedOptionId = optionId,
                textAnswer = null
            )
            current + (questionId to updated)
        }
    }

    fun setTextAnswer(questionId: String, text: String) {
        _answers.update { current ->
            val existing = current[questionId]
            val updated = (existing ?: QuizAnswerDraft()).copy(
                textAnswer = text,
                selectedOptionId = null
            )
            current + (questionId to updated)
        }
    }

    fun submitQuiz() {
        if (_isReadOnly.value) {
            _submitState.value = ViewState.Error("Quiz already submitted")
            return
        }
        val attemptId = (attempt.value as? ViewState.Success)?.data?.id
        if (attemptId.isNullOrBlank()) {
            _submitState.value = ViewState.Error("Quiz attempt not found")
            return
        }
        viewModelScope.launch {
            _submitState.value = ViewState.Loading
            val payloads = _answers.value.mapNotNull { (questionId, draft) ->
                if (draft.selectedOptionId == null && draft.textAnswer.isNullOrBlank()) {
                    null
                } else {
                    SubmitQuizAnswerRequest(
                        questionId = questionId,
                        selectedOptionId = draft.selectedOptionId,
                        textAnswer = draft.textAnswer
                    )
                }
            }
            if (payloads.isEmpty()) {
                _submitState.value = ViewState.Error("No answers to submit")
                return@launch
            }
            for (payload in payloads) {
                when (val result = submitQuizAnswerUseCase(attemptId, payload)) {
                    is ApiResult.Success -> {}
                    is ApiResult.Error -> {
                        _submitState.value = ViewState.Error(result.message)
                        return@launch
                    }
                    is ApiResult.Exception -> {
                        _submitState.value = ViewState.Error(result.e.message)
                        return@launch
                    }
                }
            }
            when (val result = submitQuizAttemptUseCase(attemptId)) {
                is ApiResult.Success -> {
                    _attempt.value = ViewState.Success(result.data)
                    _isReadOnly.value = true
                    loadQuestions(attemptId)
                    _submitState.value = ViewState.Success(result.data)
                }
                is ApiResult.Error -> _submitState.value = ViewState.Error(result.message)
                is ApiResult.Exception -> _submitState.value = ViewState.Error(result.e.message)
            }
        }
    }

    fun resetSubmitState() {
        _submitState.value = ViewState.Idle
    }

    fun explainQuestion(questionId: String, studentAnswer: String?) {
        val existing = _explanations.value[questionId]
        if (existing is ViewState.Success) {
            return
        }
        viewModelScope.launch {
            _explanations.update { current ->
                current + (questionId to ViewState.Loading)
            }
            val request = ExplainQuestionRequest(studentAnswer = studentAnswer)
            when (val result = explainQuestionUseCase(questionId, request)) {
                is ApiResult.Success -> {
                    _explanations.update { current ->
                        current + (questionId to ViewState.Success(result.data))
                    }
                }
                is ApiResult.Error -> {
                    _explanations.update { current ->
                        current + (questionId to ViewState.Error(result.message))
                    }
                }
                is ApiResult.Exception -> {
                    _explanations.update { current ->
                        current + (questionId to ViewState.Error(result.e.message))
                    }
                }
            }
        }
    }

    private fun seedAnswers(items: List<QuizAttemptQuestionItem>) {
        val seeded = items.mapNotNull { item ->
            val answer = item.answer ?: return@mapNotNull null
            item.question.id to QuizAnswerDraft(
                selectedOptionId = answer.selectedOptionId,
                textAnswer = answer.textAnswer
            )
        }.toMap()
        _answers.value = seeded
    }

    private suspend fun getFinishedAttempt(quizId: String): QuizAttemptResponse? {
        return when (val result = getQuizAttemptsForStudentUseCase(quizId)) {
            is ApiResult.Success -> {
                result.data
                    .filter { !it.endTime.isNullOrBlank() }
                    .maxByOrNull { it.endTime ?: "" }
            }
            else -> null
        }
    }
}

data class QuizAnswerDraft(
    val selectedOptionId: String? = null,
    val textAnswer: String? = null
)
