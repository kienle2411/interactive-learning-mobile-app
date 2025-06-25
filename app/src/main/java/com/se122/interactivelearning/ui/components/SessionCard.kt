package com.se122.interactivelearning.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.se122.interactivelearning.R
import com.se122.interactivelearning.data.remote.dto.SessionResponse
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SessionCard(
    session: SessionResponse,
    onJoinClick: (String) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")
    val start = ZonedDateTime.parse(session.startTime, DateTimeFormatter.ISO_DATE_TIME)
    val end = ZonedDateTime.parse(session.endTime, DateTimeFormatter.ISO_DATE_TIME)
    val now = ZonedDateTime.now()
    val canJoin = now.isAfter(start) && now.isBefore(end)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //Left - Image
            Image(
                painter = painterResource(id = R.drawable.ic_session),
                contentDescription = "Session Icon",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Mid - Context
            Column (
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = session.description ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Start: ${formatter.format(start)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Text(
                    text = "End: ${formatter.format(end)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Right - Join
            if (canJoin) {
                Button(
                    onClick = { onJoinClick(session.id) },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Join")
                }
            }
        }
    }
}

