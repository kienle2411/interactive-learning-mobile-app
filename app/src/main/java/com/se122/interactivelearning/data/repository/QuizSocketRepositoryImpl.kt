package com.se122.interactivelearning.data.repository

import com.se122.interactivelearning.data.remote.socket.quiz.QuizSocketManager
import com.se122.interactivelearning.domain.repository.QuizSocketRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizSocketRepositoryImpl @Inject constructor(
    private val quizSocketManager: QuizSocketManager
): QuizSocketRepository {
    private val _startEvent = MutableSharedFlow<Unit>(0)
    override val startEvent = _startEvent.asSharedFlow()

    override fun connect(onConnected: () -> Unit) {
        quizSocketManager.connect(onConnected)
    }

    override fun disconnect() {
        TODO("Not yet implemented")
    }

    override fun joinQuiz(quizId: String) {
        quizSocketManager.joinQuiz(quizId)
        _startEvent.tryEmit(Unit)
    }

    override fun submitAnswer(
        quizId: String,
        questionId: String,
        answer: String,
    ) {
        TODO("Not yet implemented")
    }

    override fun onReceiveQuestion(callback: (String) -> Unit) {
        quizSocketManager.onReceiveQuestion(callback)
    }

    override fun onUpdateLeaderboard(callback: (JSONObject) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun onQuizEnded(callback: (String) -> Unit) {
        TODO("Not yet implemented")
    }

}