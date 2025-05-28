package com.se122.interactivelearning.ui.screens.meeting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.se122.interactivelearning.common.ViewState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InMeetingScreen(
    id: String,
    inMeetingViewModel: InMeetingViewModel = hiltViewModel()
) {
    val meetingDetails by inMeetingViewModel.meeting.collectAsState()

    LaunchedEffect(id) {
        inMeetingViewModel.loadMeeting(id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = (meetingDetails as ViewState.Success).data.title.toString()
                    )
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row() {
                    Button(
                        onClick = {}
                    ) {}
                    Button(
                        onClick = {}
                    ) {}
                    Button(
                        onClick = {}
                    ) {}
                    Button(
                        onClick = {}
                    ) {}
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
//            LazyColumn() {
//                items(
//                    items = ,
//                    key = {  }
//                )
//            }
        }
    }
}