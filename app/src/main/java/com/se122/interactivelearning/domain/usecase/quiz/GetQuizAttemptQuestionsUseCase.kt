package com.se122.interactivelearning.domain.usecase.quiz

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.QuizAttemptQuestionItem
import com.se122.interactivelearning.domain.repository.QuizRepository
import javax.inject.Inject

class GetQuizAttemptQuestionsUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend operator fun invoke(attemptId: String): ApiResult<List<QuizAttemptQuestionItem>> {
        return quizRepository.getAttemptQuestions(attemptId)
    }
}
