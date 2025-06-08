package com.se122.interactivelearning.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.placeholder
import com.eygraber.compose.placeholder.material3.shimmer
import com.se122.interactivelearning.R
import com.se122.interactivelearning.ui.theme.GrayPrimary

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onButtonClick: () -> Unit
) {
    val user = viewModel.userState.value
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
        Text(
            text = "Upcoming Meetings",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Upcoming Assignments",
            style = MaterialTheme.typography.titleMedium
        )
    }
}
