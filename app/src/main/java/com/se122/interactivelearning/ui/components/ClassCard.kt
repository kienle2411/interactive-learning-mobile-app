package com.se122.interactivelearning.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.se122.interactivelearning.data.remote.dto.ClassroomResponse
import com.se122.interactivelearning.ui.theme.GraySecondary
import com.se122.interactivelearning.R

@Composable
fun ClassCard(
    classroom: ClassroomResponse,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(5.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                onClick()
            }
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.classroom_card),
            contentDescription = "class-card",
            modifier = Modifier.clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
        )
        Column(
            modifier = Modifier.padding(5.dp)
        ){
            Text(
                text = classroom.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Teacher: " + classroom.teacher.user.lastName + " " + classroom.teacher.user.firstName,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}