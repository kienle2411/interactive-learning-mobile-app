package com.se122.interactivelearning.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.placeholder
import com.eygraber.compose.placeholder.material3.shimmer
import com.se122.interactivelearning.R
import com.se122.interactivelearning.ui.components.HexagonIconButton
import com.se122.interactivelearning.ui.components.HomeMeetingCard
import com.se122.interactivelearning.ui.components.MultipleChoiceQuestion
import com.se122.interactivelearning.ui.theme.GrayPrimary

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onJoinMeetingClick: (String) -> Unit,
    onQuizzesClick: () -> Unit
) {
    val user = viewModel.userState.value
    val meetings = viewModel.meetingsState.value

    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            AsyncImage(
                model = user?.avatarUrl ?: R.drawable.img_avatar,
                contentDescription = "avatar_image",
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(40.dp)).border(width = 1.dp, color = GrayPrimary, shape = RoundedCornerShape(40.dp)).placeholder(
                    visible = user == null,
                    highlight = PlaceholderHighlight.shimmer()
                )
            )
            Column {
                Row {
                    Text(
                        text = "Hello, ",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "${user?.firstName} ${user?.lastName}",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.placeholder(
                            visible = user == null,
                            highlight = PlaceholderHighlight.shimmer()
                        )
                    )
                }
                Text(
                    text = "Let's learn something new today!",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Explore",
                style = MaterialTheme.typography.titleMedium
            )

            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    HexagonIconButton(
                        modifier = Modifier.weight(1f),
                        onClick = {},
                        text = "My Classrooms",
                        icon = {
                            Icon(
                                tint = Color.White,
                                painter = painterResource(R.drawable.ic_course),
                                contentDescription = "icon"
                            )
                        }
                    )
                    HexagonIconButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onQuizzesClick()
                        },
                        text = "Quizzes",
                        icon = {
                            Icon(
                                tint = Color.White,
                                painter = painterResource(R.drawable.ic_user),
                                contentDescription = "icon"
                            )
                        }
                    )
                }
            }
            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    HexagonIconButton(
                        modifier = Modifier.weight(1f),
                        onClick = {},
                        text = "My Classrooms",
                        icon = {
                            Icon(
                                tint = Color.White,
                                painter = painterResource(R.drawable.ic_course),
                                contentDescription = "icon"
                            )
                        }
                    )
                    HexagonIconButton(
                        modifier = Modifier.weight(1f),
                        onClick = {},
                        text = "Quizzes",
                        icon = {
                            Icon(
                                tint = Color.White,
                                painter = painterResource(R.drawable.ic_user),
                                contentDescription = "icon"
                            )
                        }
                    )
                }
            }
            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    HexagonIconButton(
                        modifier = Modifier.weight(1f),
                        onClick = {},
                        text = "My Classrooms",
                        icon = {
                            Icon(
                                tint = Color.White,
                                painter = painterResource(R.drawable.ic_course),
                                contentDescription = "icon"
                            )
                        }
                    )
                    HexagonIconButton(
                        modifier = Modifier.weight(1f),
                        onClick = {},
                        text = "Quizzes",
                        icon = {
                            Icon(
                                tint = Color.White,
                                painter = painterResource(R.drawable.ic_user),
                                contentDescription = "icon"
                            )
                        }
                    )
                }
            }

            Text(
                text = "Upcoming Meetings",
                style = MaterialTheme.typography.titleMedium
            )

            LazyRow (
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                items(
                    items = meetings,
                    key = { it.id }
                ) {
                    HomeMeetingCard(
                        meeting = it,
                        onJoinClick = {
                            onJoinMeetingClick(it)
                        }
                    )
                }
            }

            Text(
                text = "Upcoming Assignments",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
