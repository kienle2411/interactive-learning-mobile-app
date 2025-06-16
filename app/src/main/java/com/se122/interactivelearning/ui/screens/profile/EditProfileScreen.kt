package com.se122.interactivelearning.ui.screens.profile

import android.graphics.drawable.Icon
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.se122.interactivelearning.common.ViewState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.LineHeightStyle
import coil3.compose.AsyncImage
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.placeholder
import com.eygraber.compose.placeholder.material3.shimmer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import com.se122.interactivelearning.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    editProfileViewModel: EditProfileViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val profile by editProfileViewModel.profile.collectAsState()
    val avatar by editProfileViewModel.avatar.collectAsState()

    val firstName = remember { mutableStateOf("") }
    val lastName = remember { mutableStateOf("") }

    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var showModal by remember { mutableStateOf(false) }

    var selectedUri = remember { mutableStateOf<Uri?>(null) }

    var avatarImage = remember { mutableStateOf<String?>(null) }

    val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedUri.value = uri
            avatarImage.value = uri.toString()
        }
    }

    LaunchedEffect(Unit) {
        editProfileViewModel.loadProfile()
    }

    LaunchedEffect(profile) {
        if (profile is ViewState.Success) {
            val data = (profile as ViewState.Success).data
            firstName.value = data.firstName
            lastName.value = data.lastName
            selectedDate = convertISODateToMillis(data.dateOfBirth)
            avatarImage.value = data.avatarUrl
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconButton(
                onClick = {
                    onBackClick.invoke()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = "Edit Profile",
                style = MaterialTheme.typography.titleMedium
            )
        }
       Box(
           modifier = Modifier.fillMaxWidth(),
           contentAlignment = Alignment.Center
       ) {
           AsyncImage(
               model = avatarImage.value ?: R.drawable.img_avatar,
               contentDescription = "avatar_image",
               modifier = Modifier.size(120.dp).clip(RoundedCornerShape(10.dp)).border(1.dp, Color.Gray, RoundedCornerShape(10.dp)).placeholder(
                   visible = avatar is ViewState.Loading || profile is ViewState.Loading || profile is ViewState.Idle,
                   highlight = PlaceholderHighlight.shimmer()
               )
           )
           TextButton(
               onClick = {
                    pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
               },
               modifier = Modifier.align(Alignment.BottomCenter)
           ) {
               Text(
                   text = "Change",
                   style = MaterialTheme.typography.bodySmall,
                   color = Color.Black,
                   fontWeight = FontWeight.SemiBold
               )
           }
       }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            TextField(
                value = firstName.value,
                onValueChange = { firstName.value = it },
                label = { Text("First Name") },
                modifier = Modifier.weight(1f)
            )
            TextField(
                value = lastName.value,
                onValueChange = { lastName.value = it },
                label = { Text("Last Name") },
                modifier = Modifier.weight(1f)
            )
        }
        TextField(
            value = selectedDate?.let { convertMillisToDate(it) } ?: "",
            onValueChange = {},
            label = { Text("DOB") },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Date Picker"
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(selectedDate) {
                    awaitEachGesture {
                        awaitFirstDown(pass = PointerEventPass.Initial)
                        val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        if (upEvent != null) {
                            showModal = true
                        }
                    }
                }
        )
        if (showModal) {
            DatePickerModal(
                onDateSelected = {
                    selectedDate = it
                },
                onDismiss = {
                    showModal = false
                }
            )
        }
        Button(
            onClick = {
                editProfileViewModel.uploadAvatar(selectedUri.value!!)
                selectedUri.value = null
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            enabled = !(avatar is ViewState.Loading || profile is ViewState.Loading || profile is ViewState.Idle) && selectedUri.value != null
        ) {
            Text("Save")
        }
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

fun convertISODateToMillis(date: String): Long {
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.parse(date)?.time ?: 0
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
) {
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}