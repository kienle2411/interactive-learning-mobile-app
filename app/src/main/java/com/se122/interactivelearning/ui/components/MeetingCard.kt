package com.se122.interactivelearning.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.se122.interactivelearning.R
import com.se122.interactivelearning.data.remote.dto.MeetingResponse
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MeetingCard(
    meeting: MeetingResponse,
    onJoinClick: (String) -> Unit
) {
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    val now = ZonedDateTime.now()
    val start = ZonedDateTime.parse(meeting.startTime, formatter).withZoneSameInstant(ZoneId.systemDefault())
    val end = ZonedDateTime.parse(meeting.endTime, formatter).withZoneSameInstant(ZoneId.systemDefault())

    val statusText = when (now) {
        in start..end -> "Live"
        else -> if (end.isBefore(now)) "Past" else "Upcoming"
    }

    val statusColor = when (statusText) {
        "Live" -> Color(0xFF4CAF50)    // Green
        "Upcoming" -> Color(0xFFFFC107) // Amber
        "Past" -> Color(0xFFF44336)    // Red
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Left - Image
            Image(
                painter = painterResource(id = R.drawable.meeting_pic),
                contentDescription = "Meeting Icon",
                modifier = Modifier
                    .size(64.dp)
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Mid - Context
            Column (
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = meeting.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                meeting.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Start: ${start.format(DateTimeFormatter.ofPattern("dd MMM yyyy - HH:mm"))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "End: ${end.format(DateTimeFormatter.ofPattern("dd MMM yyyy - HH:mm"))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right: Status + Button
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = statusText,
                    color = Color.White,
                    modifier = Modifier
                        .background(color = statusColor, shape = RoundedCornerShape(16.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall
                )
                if (start.isBefore(now) && end.isAfter(now)) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        shape = RoundedCornerShape(8.dp),
                        onClick = { onJoinClick(meeting.id) },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text("Join")
                    }
                }
            }
        }
    }
}
