package com.se122.interactivelearning.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.se122.interactivelearning.common.utils.formatTimeStamp
import io.livekit.android.room.participant.Participant
import org.json.JSONObject

@Composable
fun MessageCard(
    message: String,
    senderName: String = "",
    timestamp: Long,
    participant: Participant?
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(5.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = participant?.name ?: senderName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = formatTimeStamp(timestamp),
                )
            }
            Text(
                text = message,
            )
        }
    }
}