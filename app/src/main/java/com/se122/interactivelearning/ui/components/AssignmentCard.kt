package com.se122.interactivelearning.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import com.se122.interactivelearning.data.remote.dto.AssignmentResponse
import com.se122.interactivelearning.data.remote.dto.AssignmentType
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AssignmentCard(
    assignment: AssignmentResponse
) {
    Card {
        Row() {
            Text(
                text = assignment.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(10.dp)
            )
            Text(
                text = when (ZonedDateTime.now()) {
                    in ZonedDateTime.parse(assignment.startTime.toString(), DateTimeFormatter.ISO_DATE_TIME)..ZonedDateTime.parse(assignment.dueTime.toString(), DateTimeFormatter.ISO_DATE_TIME) -> "Ongoing"
                    else -> {
                        if (ZonedDateTime.parse(assignment.dueTime.toString(), DateTimeFormatter.ISO_DATE_TIME).isBefore(ZonedDateTime.now()))
                            "Ended"
                        else
                            "Closed"
                    }
                },
                modifier = Modifier
                    .background(
                        color = if (ZonedDateTime.parse(assignment.startTime.toString(), DateTimeFormatter.ISO_DATE_TIME).isBefore(ZonedDateTime.now())
                            && ZonedDateTime.parse(assignment.dueTime.toString(), DateTimeFormatter.ISO_DATE_TIME).isAfter(ZonedDateTime.now()))
                            Color.Green else Color.Red,
                        shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                color = Color.White
            )
        }
        Text(
            text = assignment.description,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(10.dp)
        )
        HorizontalDivider()
        if (ZonedDateTime.parse(assignment.startTime.toString(), DateTimeFormatter.ISO_DATE_TIME).isBefore(ZonedDateTime.now()) && ZonedDateTime.parse(assignment.dueTime.toString(), DateTimeFormatter.ISO_DATE_TIME).isAfter(ZonedDateTime.now())) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {}
                ) {
                    when (assignment.type) {
                        AssignmentType.QUIZ -> "Start"
                        AssignmentType.ASSIGNMENT -> "Submit"
                    }
                }
            }
        }
    }
}