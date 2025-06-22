package com.se122.interactivelearning.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    Card {
        Text(
            text = session.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(10.dp)
        )
        Text(
            text = session.description ?: "",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(10.dp)
        )
        Text(
            text = session.startTime + " - " + session.endTime,
            style = MaterialTheme.typography.bodySmall
        )
        HorizontalDivider()
        if (ZonedDateTime.parse(session.startTime.toString(), DateTimeFormatter.ISO_DATE_TIME).isBefore(ZonedDateTime.now())
            && ZonedDateTime.parse(session.endTime.toString(), DateTimeFormatter.ISO_DATE_TIME).isAfter(ZonedDateTime.now())) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        onJoinClick(session.id)
                    }
                ) {
                    Text("Join")
                }
            }
        }
    }
}