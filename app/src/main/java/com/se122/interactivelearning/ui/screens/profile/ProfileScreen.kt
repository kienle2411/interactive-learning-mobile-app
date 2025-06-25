package com.se122.interactivelearning.ui.screens.profile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import coil3.compose.AsyncImage
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.shimmer
import com.eygraber.compose.placeholder.material3.placeholder
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.R
import com.se122.interactivelearning.ui.theme.GrayPrimary
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    onEditProfileClick: () -> Unit?,
    onSettingsClick: () -> Unit?,
) {
    val profile by profileViewModel.profile.collectAsState()
    var showAboutDialog by remember { mutableStateOf(false) }

    LaunchedEffect (Unit) {
        profileViewModel.loadProfile()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (showAboutDialog) {
            AlertDialog(
                onDismissRequest = { showAboutDialog = false },
                confirmButton = {
                    TextButton(onClick = { showAboutDialog = false }) {
                        Text("OK".uppercase(), style = MaterialTheme.typography.labelLarge)
                    }
                },
                title = {
                    Text(
                        text = "About this App",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "This application is the project for SE122.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Developed by:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "• 22520705 - Le Trung Kien\n• 22521148 - Nguyen Dang Kim Phung",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 8.dp,
                containerColor = MaterialTheme.colorScheme.background
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            Text(
                text = "Profile Screen",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(color = Color.LightGray).padding(20.dp)
            ) {
                AsyncImage(
                    model = if (profile is ViewState.Success && (profile as ViewState.Success).data.avatarUrl != null) (profile as ViewState.Success).data.avatarUrl else R.drawable.img_avatar,
                    contentDescription = "avatar_image",
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(80.dp)).border(width = 1.dp, color = GrayPrimary, shape = RoundedCornerShape(40.dp)).placeholder(
                        visible = profile is ViewState.Loading,
                        highlight = PlaceholderHighlight.shimmer()
                    )
                )
                Column {
                    Text(
                        text = if (profile is ViewState.Success) (profile as ViewState.Success).data.firstName + " " + (profile as ViewState.Success).data.lastName else "",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.placeholder(
                            visible = profile is ViewState.Loading,
                            highlight = PlaceholderHighlight.shimmer()
                        )
                    )
                    Text(
                        text = if (profile is ViewState.Success) (profile as ViewState.Success).data.email else "",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.placeholder(
                            visible = profile is ViewState.Loading,
                            highlight = PlaceholderHighlight.shimmer()
                        )
                    )
                }
            }
        }
        HorizontalDivider()
        RowButton(
            imageVector = Icons.Default.Person,
            text = "Edit Profile",
            onClick = {
                onEditProfileClick()
            }
        )
//        RowButton(
//            imageVector = Icons.Default.Settings,
//            text = "Settings",
//            onClick = {
//                onSettingsClick()
//            }
//        )
        RowButton(
            imageVector = Icons.Default.Info,
            text = "About This App",
            onClick = {
                showAboutDialog = !showAboutDialog
            }
        )
        HorizontalDivider()
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
            modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(
                        onClick = {
                            profileViewModel.logout()
                        }
                    )
                    .background(color = Color.LightGray)
                    .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "logout"
            )
            Text(
                text = "Logout",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun RowButton(
    imageVector: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(
                onClick = {
                    onClick.invoke()
                }
            )
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = text
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "arrow_right"
        )
    }
}