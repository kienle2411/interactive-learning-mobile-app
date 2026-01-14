package com.se122.interactivelearning.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.se122.interactivelearning.R
import com.se122.interactivelearning.data.remote.dto.SessionResponse
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeSessionCard(
    session: SessionResponse,
    onJoinClick: (String) -> Unit
) {
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    val now = ZonedDateTime.now()
    val start = ZonedDateTime.parse(session.startTime, formatter).withZoneSameInstant(ZoneId.systemDefault())
    val end = ZonedDateTime.parse(session.endTime, formatter).withZoneSameInstant(ZoneId.systemDefault())

    val status = when (now) {
        in start..end -> "Live"
        else -> if (end.isBefore(now)) "Past" else "Upcoming"
    }

    val statusColor = when (status) {
        "Live" -> Color(0xFF4CAF50)
        "Upcoming" -> Color(0xFF2196F3)
        else -> Color(0xFFF44336)
    }

    Card (
        modifier = Modifier
            .width(300.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = R.drawable.img_session_placeholder),
                contentDescription = "Session Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                session.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Text(
                    text = "${start.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))} → ${end.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 6.dp)
                )

                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = status,
                        modifier = Modifier
                            .background(statusColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        color = Color.White
                    )

                    if (status == "Live") {
                        Button (
                            onClick = { onJoinClick(session.id) }
                        ) {
                            Text("Join")
                        }
                    }
                }
            }
        }
    }
}
