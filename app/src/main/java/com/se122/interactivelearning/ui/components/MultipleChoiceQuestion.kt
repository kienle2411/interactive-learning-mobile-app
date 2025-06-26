package com.se122.interactivelearning.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.se122.interactivelearning.data.remote.dto.QuestionResponse

@Composable
fun MultipleChoiceQuestion(questionData: QuestionResponse) {
    var selectedOptionId by remember { mutableStateOf<String?>(null) }
    var submitted by remember { mutableStateOf(false) }

    val visibleOptions = questionData.questionOption.filter { it.deletedAt == null }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = questionData.content, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        AssistChip(
            modifier = Modifier.align(Alignment.End),
            onClick = {},
            label = {
                Text(
                    text = "${questionData.score} points"
                )
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        visibleOptions.forEach { option ->
            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !submitted) {
                        selectedOptionId = option.id
                    }
                    .padding(vertical = 4.dp)
            ) {
                RadioButton(
                    selected = selectedOptionId == option.id,
                    onClick = { if (!submitted) selectedOptionId = option.id }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = option.option)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button (
            onClick = { submitted = true },
            enabled = selectedOptionId != null && !submitted
        ) {
            Text("Submit")
        }

        if (submitted && selectedOptionId != null) {
            val isCorrect = visibleOptions.find { it.id == selectedOptionId }?.isCorrect ?: false
            Text(
                text = if (isCorrect) "Correct!" else "❌ Incorrect.",
                color = if (isCorrect) Color.Green else Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
