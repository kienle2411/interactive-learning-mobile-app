package com.se122.interactivelearning.data.repository

import com.se122.interactivelearning.data.remote.dto.UserResponse
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
    override fun connect(onConnected: () -> Unit) {
        quizSocketManager.connect(onConnected)
    }

    override fun disconnect() {
        quizSocketManager.disconnect()
    }

    override fun joinQuiz(quizId: String) {
        quizSocketManager.joinQuiz(quizId)
    }

    override fun submitAnswer(
        quizId: String,
        questionId: String,
        answer: String,
    ) {
        TODO("Not yet implemented")
    }

    override fun onQuizStarted(callback: () -> Unit) {
        quizSocketManager.onQuizStarted(callback)
    }

    override fun onReceiveQuestion(callback: (String) -> Unit) {
        quizSocketManager.onReceiveQuestion(callback)
    }

    override fun onUpdateLeaderboard(callback: (JSONObject) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun onQuizEnded(callback: (String) -> Unit) {
        quizSocketManager.onQuizEnded(callback)
    }

    override fun onRoomJoined(callback: (String, UserResponse) -> Unit) {
        quizSocketManager.onRoomJoined(callback)
    }
}