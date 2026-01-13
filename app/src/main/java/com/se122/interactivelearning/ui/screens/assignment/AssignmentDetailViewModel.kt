package com.se122.interactivelearning.ui.screens.assignment

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.AssignmentAnswerRequest
import com.se122.interactivelearning.data.remote.dto.AssignmentQuestionsResponse
import com.se122.interactivelearning.data.remote.dto.SubmissionResponse
import com.se122.interactivelearning.data.remote.dto.SubmitAssignmentRequest
import com.se122.interactivelearning.domain.usecase.assignment.GetAssignmentSubmissionsUseCase
import com.se122.interactivelearning.domain.usecase.assignment.GetAssignmentQuestionsUseCase
import com.se122.interactivelearning.domain.usecase.assignment.SubmitAssignmentFilesUseCase
import com.se122.interactivelearning.domain.usecase.assignment.SubmitAssignmentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class AssignmentDetailViewModel @Inject constructor(
    private val getAssignmentQuestionsUseCase: GetAssignmentQuestionsUseCase,
    private val submitAssignmentUseCase: SubmitAssignmentUseCase,
    private val submitAssignmentFilesUseCase: SubmitAssignmentFilesUseCase,
    private val getAssignmentSubmissionsUseCase: GetAssignmentSubmissionsUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _assignment = MutableStateFlow<ViewState<AssignmentQuestionsResponse>>(ViewState.Loading)
    val assignment = _assignment.asStateFlow()

    private val _answers = MutableStateFlow<Map<String, AssignmentAnswerDraft>>(emptyMap())
    val answers = _answers.asStateFlow()

    private val _submitState = MutableStateFlow<ViewState<SubmissionResponse>>(ViewState.Idle)
    val submitState = _submitState.asStateFlow()

    private val _submissions = MutableStateFlow<ViewState<List<SubmissionResponse>>>(ViewState.Idle)
    val submissions = _submissions.asStateFlow()

    fun loadAssignmentQuestions(id: String) {
        viewModelScope.launch {
            _assignment.value = ViewState.Loading
            when (val result = getAssignmentQuestionsUseCase(id)) {
                is ApiResult.Success -> _assignment.value = ViewState.Success(result.data)
                is ApiResult.Error -> _assignment.value = ViewState.Error(result.message)
                is ApiResult.Exception -> _assignment.value = ViewState.Error(result.e.message)
            }
        }
    }

    fun loadSubmissions(id: String) {
        viewModelScope.launch {
            _submissions.value = ViewState.Loading
            when (val result = getAssignmentSubmissionsUseCase(id)) {
                is ApiResult.Success -> _submissions.value = ViewState.Success(result.data)
                is ApiResult.Error -> _submissions.value = ViewState.Error(result.message)
                is ApiResult.Exception -> _submissions.value = ViewState.Error(result.e.message)
            }
        }
    }

    fun setOptionAnswer(questionId: String, optionId: String) {
        _answers.update { current ->
            val existing = current[questionId]
            val updated = (existing ?: AssignmentAnswerDraft()).copy(
                selectedOptionId = optionId,
                textAnswer = null
            )
            current + (questionId to updated)
        }
    }

    fun setTextAnswer(questionId: String, text: String) {
        _answers.update { current ->
            val existing = current[questionId]
            val updated = (existing ?: AssignmentAnswerDraft()).copy(
                textAnswer = text,
                selectedOptionId = null
            )
            current + (questionId to updated)
        }
    }

    fun submitAssignment(assignmentId: String, files: List<Uri>) {
        viewModelScope.launch {
            _submitState.value = ViewState.Loading
            val payload = _answers.value.mapNotNull { (questionId, draft) ->
                if (draft.selectedOptionId == null && draft.textAnswer.isNullOrBlank()) {
                    null
                } else {
                    AssignmentAnswerRequest(
                        questionId = questionId,
                        selectedOptionId = draft.selectedOptionId,
                        textAnswer = draft.textAnswer
                    )
                }
            }
            val request = SubmitAssignmentRequest(
                answers = payload.takeIf { it.isNotEmpty() }
            )
            if (request.answers == null && files.isEmpty()) {
                _submitState.value = ViewState.Error("No answers or files to submit")
                return@launch
            }
            if (request.answers != null) {
                when (val result = submitAssignmentUseCase(assignmentId, request)) {
                    is ApiResult.Success -> _submitState.value = ViewState.Success(result.data)
                    is ApiResult.Error -> _submitState.value = ViewState.Error(result.message)
                    is ApiResult.Exception -> _submitState.value = ViewState.Error(result.e.message)
                }
            }
            if (files.isNotEmpty()) {
                val parts = buildFileParts(files)
                if (parts.isEmpty()) {
                    _submitState.value = ViewState.Error("No files selected")
                    return@launch
                }
                when (val result = submitAssignmentFilesUseCase(assignmentId, parts)) {
                    is ApiResult.Success -> _submitState.value = ViewState.Success(result.data)
                    is ApiResult.Error -> _submitState.value = ViewState.Error(result.message)
                    is ApiResult.Exception -> _submitState.value = ViewState.Error(result.e.message)
                }
            }
            loadSubmissions(assignmentId)
        }
    }

    fun resetSubmitState() {
        _submitState.value = ViewState.Idle
    }

    private fun buildFileParts(files: List<Uri>): List<MultipartBody.Part> {
        return files.mapNotNull { uri ->
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"
            if (bytes == null) {
                null
            } else {
                MultipartBody.Part.createFormData(
                    "files",
                    uri.lastPathSegment ?: "assignment_file",
                    bytes.toRequestBody(mimeType.toMediaTypeOrNull())
                )
            }
        }
    }
}

data class AssignmentAnswerDraft(
    val selectedOptionId: String? = null,
    val textAnswer: String? = null
)
