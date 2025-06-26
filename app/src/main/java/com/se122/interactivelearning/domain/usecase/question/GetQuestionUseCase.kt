package com.se122.interactivelearning.domain.usecase.question

import android.util.Log
import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.QuestionResponse
import com.se122.interactivelearning.domain.repository.QuestionRepository
import javax.inject.Inject

class GetQuestionUseCase @Inject constructor(
    private val questionRepository: QuestionRepository
) {
    suspend operator fun invoke(id: String): ApiResult<QuestionResponse> {
        Log.i("GetQuestionUseCase", "invoke: $id")
        return questionRepository.getQuestion(id)
    }
}