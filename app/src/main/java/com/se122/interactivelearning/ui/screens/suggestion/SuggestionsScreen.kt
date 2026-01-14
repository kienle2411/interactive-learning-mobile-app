package com.se122.interactivelearning.ui.screens.suggestion

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.placeholder
import com.eygraber.compose.placeholder.material3.shimmer
import com.se122.interactivelearning.R
import com.se122.interactivelearning.data.remote.dto.SuggestedActionResponse
import com.se122.interactivelearning.ui.screens.home.HomeViewModel
import androidx.compose.ui.res.painterResource

@Composable
fun SuggestionsScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onSuggestionClick: (SuggestedActionResponse) -> Unit
) {
    val overallSuggestions = viewModel.overallSuggestionsState.value
    val classroomSuggestions = viewModel.classroomSuggestionsState.value
    val classrooms = viewModel.classroomsState.value
    val isLoadingSuggestions = viewModel.isLoadingSuggestions.value

    LaunchedEffect(Unit) {
        viewModel.loadClassroomsAndSuggestions()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .padding(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "AI Suggestions",
            style = MaterialTheme.typography.titleMedium
        )

        if (isLoadingSuggestions) {
            SuggestionsPlaceholderList()
        } else if (!overallSuggestions?.suggestions.isNullOrEmpty()) {
            Text(
                text = "Overall AI Suggestions",
                style = MaterialTheme.typography.titleSmall
            )
            overallSuggestions?.overallMastery?.let { mastery ->
                val masteryPercent = (mastery * 100).toInt()
                Text(
                    text = "Overall mastery: ${masteryPercent}%",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            SuggestionsList(
                suggestions = overallSuggestions?.suggestions ?: emptyList(),
                onSuggestionClick = onSuggestionClick
            )
        }

        if (!isLoadingSuggestions) {
            val classroomsWithSuggestions = classrooms.mapNotNull { classroomWrapper ->
                val classroomId = classroomWrapper.classroom.id
                val suggestions = classroomSuggestions[classroomId]?.suggestions.orEmpty()
                if (suggestions.isEmpty()) {
                    null
                } else {
                    classroomWrapper to suggestions
                }
            }

            classroomsWithSuggestions.forEach { (classroomWrapper, suggestions) ->
                Text(
                    text = "Suggestions for ${classroomWrapper.classroom.name}",
                    style = MaterialTheme.typography.titleSmall
                )
                SuggestionsList(
                    suggestions = suggestions,
                    onSuggestionClick = onSuggestionClick
                )
            }
        }
    }
}

@Composable
private fun SuggestionsList(
    suggestions: List<SuggestedActionResponse>,
    onSuggestionClick: (SuggestedActionResponse) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        suggestions.forEach { suggestion ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSuggestionClick(suggestion) },
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    if (suggestion.type == "LESSON") {
                        Image(
                            painter = painterResource(id = R.drawable.img_lesson_placeholder),
                            contentDescription = "Lesson placeholder",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = suggestion.title,
                            style = MaterialTheme.typography.titleSmall
                        )
                        suggestion.subtitle?.let { subtitle ->
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Text(
                            text = suggestion.reason,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SuggestionsPlaceholderList() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        repeat(3) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .placeholder(
                            visible = true,
                            highlight = PlaceholderHighlight.shimmer()
                        ),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Suggestion title",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Suggestion subtitle",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Suggestion reason",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
