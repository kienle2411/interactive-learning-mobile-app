package com.se122.interactivelearning.ui.screens.course

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.ui.theme.GraySecondary
import com.se122.interactivelearning.ui.theme.GreenPrimary
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.se122.interactivelearning.ui.components.MaterialCard
import com.se122.interactivelearning.ui.components.StudentCard
import androidx.core.net.toUri
import com.se122.interactivelearning.ui.components.AssignmentCard
import com.se122.interactivelearning.ui.components.MeetingCard
import com.se122.interactivelearning.ui.components.SessionCard

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CourseDetailScreen(
    id: String,
    viewModel: CourseDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
) {
    val classroomDetails by viewModel.classroomDetails.collectAsState()
    val classroomStudents by viewModel.classroomStudents.collectAsState()
    val classroomMaterials by viewModel.classroomMaterials.collectAsState()
    val classroomSessions by viewModel.classroomSessions.collectAsState()
    val classroomMeetings by viewModel.classroomMeetings.collectAsState()
    val classroomAssignments by viewModel.classroomAssignments.collectAsState()
    val fileDownloadLink by viewModel.fileDownloadLink.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(id) {
        viewModel.loadCourseDetails(id)
        viewModel.loadStudentList(id)
        viewModel.loadMaterials(id)
        viewModel.loadSessions(id)
    }

    LaunchedEffect(fileDownloadLink) {
        if (fileDownloadLink is ViewState.Success) {
            val intent = Intent(Intent.ACTION_VIEW, (fileDownloadLink as ViewState.Success).data.toUri())
            context.startActivity(intent)
        }
    }

    when (classroomDetails) {
        is ViewState.Error -> {
            val msg = (classroomDetails as ViewState.Error).message ?: "Unknown error"
            Text(msg, color = androidx.compose.ui.graphics.Color.Red)
        }
        is ViewState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        is ViewState.Success -> {
            val classroomDetails = (classroomDetails as ViewState.Success).data
            var tabIndex by remember { mutableIntStateOf(0) }
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            onBackClick()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    Text(
                        text = classroomDetails.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                ScrollableTabRow(
                    selectedTabIndex = tabIndex,
                    edgePadding = 0.dp,
                    indicator = { tabPositions ->
                        Box(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[tabIndex])
                                .height(4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .padding(horizontal = 28.dp)
                                .background(color = GreenPrimary)
                        )
                    },
                    divider = {}
                ) {
                    Tab(
                        selected = tabIndex == 0,
                        onClick = { tabIndex = 0 },
                        selectedContentColor = GreenPrimary,
                        unselectedContentColor = GraySecondary,
                        text = { Text("Session") }
                    )
                    Tab(
                        selected = tabIndex == 1,
                        onClick = { tabIndex = 1 },
                        selectedContentColor = GreenPrimary,
                        unselectedContentColor = GraySecondary,
                        text = { Text("Meeting") }
                    )
                    Tab(
                        selected = tabIndex == 2,
                        onClick = { tabIndex = 2 },
                        selectedContentColor = GreenPrimary,
                        unselectedContentColor = GraySecondary,
                        text = { Text("Assignment") }
                    )
                    Tab(
                        selected = tabIndex == 3,
                        onClick = { tabIndex = 3 },
                        selectedContentColor = GreenPrimary,
                        unselectedContentColor = GraySecondary,
                        text = { Text("Material") }
                    )
                    Tab(
                        selected = tabIndex == 4,
                        onClick = { tabIndex = 4 },
                        selectedContentColor = GreenPrimary,
                        unselectedContentColor = GraySecondary,
                        text = { Text("Information") }
                    )
                }
                when (tabIndex) {
                    0 -> {
                        when (classroomSessions) {
                            is ViewState.Success -> {
                                val classroomSessions = (classroomSessions as ViewState.Success).data
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(
                                        items = classroomSessions,
                                        key = { it -> it.id }
                                    ) {
                                        SessionCard(
                                            session = it
                                        )
                                    }
                                }
                            }
                            is ViewState.Error -> {}
                            is ViewState.Loading -> {
                                CircularProgressIndicator()
                            }
                            is ViewState.Idle -> {}
                        }
                    }
                    1 -> {
                        when (classroomMeetings) {
                            is ViewState.Success -> {
                                val classroomMeetings = (classroomMeetings as ViewState.Success).data
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(
                                        items = classroomMeetings,
                                        key = { it -> it.id }
                                    ) {
                                        MeetingCard(
                                            meeting = it
                                        )
                                    }
                                }
                            }
                            is ViewState.Error -> {}
                            is ViewState.Loading -> {
                                CircularProgressIndicator()
                            }
                            is ViewState.Idle -> {}
                        }
                    }
                    2 -> {
                        when (classroomAssignments) {
                            is ViewState.Success -> {
                                val classroomAssignments = (classroomAssignments as ViewState.Success).data
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(
                                        items = classroomAssignments,
                                        key = { it -> it.id }
                                    ) {
                                        AssignmentCard(
                                            assignment = it
                                        )
                                    }
                                }
                            }
                            is ViewState.Error -> {}
                            is ViewState.Loading -> {
                                CircularProgressIndicator()
                            }
                            is ViewState.Idle -> {}
                        }
                    }
                    3 -> {
                        when (classroomMaterials) {
                            is ViewState.Success -> {
                                val classroomMaterials = (classroomMaterials as ViewState.Success).data
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(
                                        items = classroomMaterials,
                                        key = { it -> it.id }
                                    ) {
                                        MaterialCard(
                                            material = it,
                                            onDownloadClick = {
                                                viewModel.getFileDownloadLink(it)
                                            }
                                        )
                                    }
                                }
                            }
                            is ViewState.Error -> {

                            }
                            is ViewState.Loading -> {
                                CircularProgressIndicator()
                            }
                            is ViewState.Idle -> {

                            }
                        }
                    }
                    4 -> {
                        when (classroomStudents) {
                            is ViewState.Success -> {
                                val classroomStudents = (classroomStudents as ViewState.Success).data
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = classroomDetails.description,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = classroomDetails.description,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    HorizontalDivider()
                                    Text(
                                        text = "Teacher",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    LazyColumn(
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        item {
                                            Text(
                                                text = "Participants",
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                        items(
                                            items = classroomStudents,
                                            key = { it -> it.id }
                                        ) {
                                            StudentCard(student = it.student)
                                        }
                                    }
                                }
                            }
                            is ViewState.Error -> {
                                val msg = (classroomStudents as ViewState.Error).message ?: "Unknown error"
                                Text(msg, color = androidx.compose.ui.graphics.Color.Red)
                            }
                            is ViewState.Loading -> {
                                CircularProgressIndicator()
                            }
                            is ViewState.Idle -> {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
        is ViewState.Idle -> {
            CircularProgressIndicator()
        }
    }
}