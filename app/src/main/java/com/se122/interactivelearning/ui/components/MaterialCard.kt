package com.se122.interactivelearning.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.se122.interactivelearning.data.remote.dto.MaterialResponse
import com.se122.interactivelearning.R

@Composable
fun MaterialCard(
    material: MaterialResponse,
    onDownloadClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.heightIn(max = 200.dp).fillMaxWidth(),
    ) {
        Text(
            text = material.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(10.dp)
        )
        Text(
            text = material.description,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(10.dp)
        )
        if (!material.file.isEmpty()) {
            LazyColumn {
                item {
                    HorizontalDivider()
                }
                items(
                    items = material.file,
                    key = { it.id }
                ) {
                    Card(
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Column {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = it.name,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                                if (it.url.isNotEmpty()) {
                                    IconButton(
                                        onClick = {
                                            onDownloadClick(it.id)
                                        },
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_download),
                                            contentDescription = "download-icon"
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
}