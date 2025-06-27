package com.se122.interactivelearning.ui.screens.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun QuizSummaryScreen(
    viewModel: QuizSummaryViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val leaderboard by viewModel.leaderboard.collectAsState()
    val profile by viewModel.profile.collectAsState()

    DisposableEffect(Unit) {
        viewModel.getLeaderboard()
        onDispose {
            viewModel.disconnect()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Leaderboard",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Column {
            if (leaderboard.isNotEmpty()) {
                val top3 = leaderboard.take(3)
                val others = leaderboard.drop(3)

                top3.forEachIndexed { index, user ->
                    val medal = when (index) {
                        0 -> "🥇"
                        1 -> "🥈"
                        2 -> "🥉"
                        else -> ""
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).shadow(10.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = "$medal ${user.username} - ${user.score} ${if (user.username == profile?.username) " (You)" else ""}",
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Spacer(
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                if (others.isNotEmpty()) {
                    Text(
                        text = "Other users:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        Text("Rank", modifier = Modifier.weight(1f))
                        Text("Player", modifier = Modifier.weight(3f))
                        Text("Score", modifier = Modifier.weight(2f))
                    }

                    LazyColumn {
                        itemsIndexed(others) { index, user ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Text("${index + 4}", modifier = Modifier.weight(1f))
                                Text(user.username, modifier = Modifier.weight(3f))
                                Text(user.score.toString(), modifier = Modifier.weight(2f))
                            }
                        }
                    }
                }
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModel.disconnect()
                onBack()
            }
        ) {
            Text(
                text = "Back",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}