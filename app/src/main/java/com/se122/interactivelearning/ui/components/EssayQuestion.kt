package com.se122.interactivelearning.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.se122.interactivelearning.data.remote.dto.AnswerRequest
import com.se122.interactivelearning.data.remote.dto.QuestionResponse
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun EssayQuestion(
    questionData: QuestionResponse,
    onQuestionAnswered: (answer: String) -> Unit
) {
    val answer = remember { mutableStateOf(TextFieldValue("")) }
    val submitted = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = questionData.content,
            style = MaterialTheme.typography.titleMedium
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = answer.value,
            onValueChange = {
                answer.value = it
            },
            label = { Text("Enter your answer") }
        )
        if (!submitted.value) {
            Button(
                onClick = {
                    onQuestionAnswered.invoke(answer.value.text)
                    submitted.value = true
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                enabled = answer.value.text.isNotBlank()
            ) {
                Text(
                    "Submit"
                )
            }
        }
    }
}