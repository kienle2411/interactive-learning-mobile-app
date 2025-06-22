package com.se122.interactivelearning.data.repository

import com.se122.interactivelearning.data.remote.socket.quiz.QuizSocketManager
import com.se122.interactivelearning.domain.repository.QuizSocketRepository
import org.json.JSONObject
import javax.inject.Inject

class QuizSocketRepositoryImpl @Inject constructor(
    private val quizSocketManager: QuizSocketManager
): QuizSocketRepository {
    override fun connect(onConnected: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun disconnect() {
        TODO("Not yet implemented")
    }

    override fun joinQuiz(quizId: String) {
        TODO("Not yet implemented")
    }

    override fun submitAnswer(
        quizId: String,
        questionId: String,
        answer: String,
    ) {
        TODO("Not yet implemented")
    }

    override fun onReceiveQuestion(callback: (String, String) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun onUpdateLeaderboard(callback: (JSONObject) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun onQuizEnded(callback: (String) -> Unit) {
        TODO("Not yet implemented")
    }

}