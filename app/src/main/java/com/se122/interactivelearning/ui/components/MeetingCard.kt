package com.se122.interactivelearning.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.se122.interactivelearning.data.remote.dto.MeetingResponse
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MeetingCard(
    meeting: MeetingResponse
) {
    Card() {
        Text(
            text = meeting.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(10.dp)
        )
        if (meeting.description != null) {
            Text(
                text = meeting.description,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(10.dp)
            )
        }
        HorizontalDivider()
        Row(
            modifier = Modifier.padding(10.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = meeting.startTime + " - " + meeting.endTime,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = when (ZonedDateTime.now()) {
                    in ZonedDateTime.parse(meeting.startTime.toString(), DateTimeFormatter.ISO_DATE_TIME)..ZonedDateTime.parse(meeting.endTime.toString(), DateTimeFormatter.ISO_DATE_TIME) -> "Live"
                    else -> {
                        if (ZonedDateTime.parse(meeting.endTime.toString(), DateTimeFormatter.ISO_DATE_TIME).isBefore(ZonedDateTime.now()))
                            "Past"
                        else
                            "Upcoming"
                    }
                },
                modifier = Modifier
                    .background(
                        color = if (ZonedDateTime.parse(meeting.startTime.toString(), DateTimeFormatter.ISO_DATE_TIME).isBefore(ZonedDateTime.now())
                            && ZonedDateTime.parse(meeting.endTime.toString(), DateTimeFormatter.ISO_DATE_TIME).isAfter(ZonedDateTime.now()))
                            Color.Green else Color.Red,
                        shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                color = Color.White
            )
        }
        if (ZonedDateTime.parse(meeting.startTime.toString(), DateTimeFormatter.ISO_DATE_TIME).isBefore(ZonedDateTime.now()) && ZonedDateTime.parse(meeting.endTime.toString(), DateTimeFormatter.ISO_DATE_TIME).isAfter(ZonedDateTime.now())) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {

                    },
                    modifier = Modifier.fillMaxWidth(1f / 3f)
                ) {
                    Text(text = "Join")
                }
            }

        }
    }
}