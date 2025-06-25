package com.se122.interactivelearning.ui.components

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import android.net.Uri
import com.se122.interactivelearning.data.remote.dto.MaterialResponse
import com.se122.interactivelearning.R

//@Composable
//fun MaterialCard(
//    material: MaterialResponse,
//    onDownloadClick: (String) -> Unit
//) {
//    Card(
//        modifier = Modifier.heightIn(max = 200.dp).fillMaxWidth(),
//    ) {
//        Text(
//            text = material.title,
//            style = MaterialTheme.typography.bodyMedium,
//            fontWeight = FontWeight.SemiBold,
//            modifier = Modifier.padding(10.dp)
//        )
//        Text(
//            text = material.description,
//            style = MaterialTheme.typography.bodySmall,
//            modifier = Modifier.padding(10.dp)
//        )
//        if (!material.file.isEmpty()) {
//            LazyColumn {
//                item {
//                    HorizontalDivider()
//                }
//                items(
//                    items = material.file,
//                    key = { it.id }
//                ) {
//                    Card(
//                        modifier = Modifier.padding(10.dp)
//                    ) {
//                        Column {
//                            Row(
//                                horizontalArrangement = Arrangement.SpaceBetween,
//                                verticalAlignment = Alignment.CenterVertically,
//                                modifier = Modifier.fillMaxWidth()
//                            ) {
//                                Text(
//                                    text = it.name,
//                                    style = MaterialTheme.typography.bodySmall,
//                                )
//                                if (it.url.isNotEmpty()) {
//                                    IconButton(
//                                        onClick = {
//                                            onDownloadClick(it.id)
//                                        },
//                                    ) {
//                                        Icon(
//                                            painter = painterResource(R.drawable.ic_download),
//                                            contentDescription = "download-icon"
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}

@Composable
fun MaterialCard(
    material: MaterialResponse,
    onDownloadClick: (String) -> Unit
) {
    val context = LocalContext.current
    val isLink = material.description.startsWith("http://") || material.description.startsWith("https://")


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // Top: Icon - Context
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Icon
                Image(
                    painter = painterResource(id = R.drawable.ic_folder),
                    contentDescription = "Folder Icon",
                    modifier = Modifier
                        .size(48.dp)
                        .padding(end = 12.dp)
                )

                // Context
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = material.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (isLink) {
                        val annotatedText = buildAnnotatedString {
                            pushStringAnnotation(tag = "URL", annotation = material.description)
                            withStyle(
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF1E88E5),
                                    textDecoration = TextDecoration.Underline
                                ).toSpanStyle()
                            ) {
                                append(material.description)
                            }
                            pop()
                        }

                        ClickableText(
                            text = annotatedText,
                            onClick = { offset ->
                                annotatedText.getStringAnnotations("URL", offset, offset)
                                    .firstOrNull()?.let { annotation ->
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                                        context.startActivity(intent)
                                    }
                            }
                        )
                    } else {
                        Text(
                            text = material.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            // If file - show download/fileName
            if (material.file.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider()

                Column {
                    material.file.forEach { file ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = file.name,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f)
                            )
                            if (file.url.isNotEmpty()) {
                                IconButton(
                                    onClick = { onDownloadClick(file.id) }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_download),
                                        contentDescription = "Download File"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
