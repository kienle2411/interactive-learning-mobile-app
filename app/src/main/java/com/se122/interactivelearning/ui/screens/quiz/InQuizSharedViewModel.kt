package com.se122.interactivelearning.ui.screens.quiz

import androidx.lifecycle.ViewModel
import com.se122.interactivelearning.data.remote.socket.quiz.QuizSocketManager
import com.se122.interactivelearning.domain.repository.QuizSocketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InQuizSharedViewModel @Inject constructor(
    private val quizSocketRepository: QuizSocketRepository
): ViewModel() {

    fun joinQuiz(quizId: String) {
        quizSocketRepository.joinQuiz(quizId)
    }

    fun submitAnswer(quizId: String, questionId: String, answer: String) {
        quizSocketRepository.submitAnswer(quizId, questionId, answer)
    }

}