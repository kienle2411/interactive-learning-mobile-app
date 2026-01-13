package com.se122.interactivelearning.ui.components

import android.graphics.ColorFilter
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.se122.interactivelearning.data.remote.dto.StudentResponse
import com.se122.interactivelearning.R

@Composable
fun StudentCard(
    student: StudentResponse,
    score: Int?,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AsyncImage(
                    model = student.user.avatarUrl?.takeIf { it.isNotEmpty() } ?: R.drawable.img_avatar,
                    contentDescription = "avatar",
                    modifier = Modifier.size(50.dp).clip(RoundedCornerShape(50.dp)),
                )
                Text(
                    text = student.user.firstName + " " + student.user.lastName,
                )
            }
            Text(
                text = score?.toString() ?: "0",
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}