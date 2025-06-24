package com.se122.interactivelearning.ui.screens.course

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.ui.theme.GraySecondary
import com.se122.interactivelearning.ui.theme.GreenPrimary
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import com.se122.interactivelearning.ui.components.MaterialCard
import com.se122.interactivelearning.ui.components.StudentCard
import androidx.core.net.toUri
import com.se122.interactivelearning.ui.components.AssignmentCard
import com.se122.interactivelearning.ui.components.GroupCard
import com.se122.interactivelearning.ui.components.MeetingCard
import com.se122.interactivelearning.ui.components.SessionCard
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    id: String,
    viewModel: CourseDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onJoinMeetingClick: (String) -> Unit,
    onJoinSessionClick: (String) -> Unit
) {
    val classroomDetails by viewModel.classroomDetails.collectAsState()
    val classroomStudents by viewModel.classroomStudents.collectAsState()
    val classroomMaterials by viewModel.classroomMaterials.collectAsState()
    val classroomSessions by viewModel.classroomSessions.collectAsState()
    val classroomMeetings by viewModel.classroomMeetings.collectAsState()
    val classroomAssignments by viewModel.classroomAssignments.collectAsState()
    val fileDownloadLink by viewModel.fileDownloadLink.collectAsState()
    val groupsState by viewModel.groupsState.collectAsState()


    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    val tabs = listOf("Session", "Meeting", "Groups", "Material", "Information")
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { tabs.size }
    )

    val refreshState = rememberPullToRefreshState()
    val refreshingStates = remember {
        List(tabs.size) {
            mutableStateOf(false)
        }
    }

    LaunchedEffect(id) {
        viewModel.loadCourseDetails(id)
        viewModel.loadStudentList(id)
        viewModel.loadMaterials(id)
        viewModel.loadSessions(id)
        viewModel.loadMeetings(id)
        viewModel.loadGroups(id)
        //viewModel.loadAssignments(id)
    }

    LaunchedEffect(fileDownloadLink) {
        if (fileDownloadLink is ViewState.Success) {
            val intent = Intent(Intent.ACTION_VIEW, (fileDownloadLink as ViewState.Success).data.toUri())
            context.startActivity(intent)
        }
    }

    LaunchedEffect(classroomSessions) {
        if (pagerState.currentPage == 0) {
            when (classroomSessions) {
                is ViewState.Success, is ViewState.Error -> refreshingStates[0].value = false
                else -> {}
            }
        }
    }

    LaunchedEffect(classroomMeetings) {
        if (pagerState.currentPage == 1) {
            when (classroomMeetings) {
                is ViewState.Success, is ViewState.Error -> refreshingStates[1].value = false
                else -> {}
            }
        }
    }

//    LaunchedEffect(classroomAssignments) {
//        if (pagerState.currentPage == 2) {
//            when (classroomAssignments) {
//                is ViewState.Success, is ViewState.Error -> refreshingStates[2].value = false
//                else -> {}
//            }
//        }
//    }

    LaunchedEffect(groupsState) {
        if (pagerState.currentPage == 2) {
            when (groupsState) {
                is ViewState.Success, is ViewState.Error -> refreshingStates[2].value = false
                else -> {}
            }
        }
    }

    LaunchedEffect(classroomMaterials) {
        if (pagerState.currentPage == 3) {
            when (classroomMaterials) {
                is ViewState.Success, is ViewState.Error -> refreshingStates[3].value = false
                else -> {}
            }
        }
    }

    LaunchedEffect(classroomStudents) {
        if (pagerState.currentPage == 4) {
            when (classroomStudents) {
                is ViewState.Success, is ViewState.Error -> refreshingStates[4].value = false
                else -> {}
            }
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
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
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
                    IconButton(
                        onClick = {},
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share"
                        )
                    }
                }
                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    edgePadding = 0.dp,
                    indicator = { tabPositions ->
                        if (tabPositions.size > pagerState.currentPage) {
                            Box(
                                modifier = Modifier
                                    .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                                    .clip(RoundedCornerShape(10.dp))
                                    .padding(horizontal = 28.dp)
                                    .height(3.dp)
                                    .background(color = GreenPrimary)
                            )
                        }
                    },
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = { Text(title) },
                            selectedContentColor = GreenPrimary,
                            unselectedContentColor = GraySecondary,
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize().weight(1f)
                ) {
                    val isRefreshing = refreshingStates[it]
                    PullToRefreshBox(
                        modifier = Modifier.fillMaxSize(),
                        isRefreshing = isRefreshing.value,
                        onRefresh = {
                            isRefreshing.value = true
                            coroutineScope.launch {
                                when (it) {
                                    0 -> viewModel.loadSessions(id)
                                    1 -> viewModel.loadMeetings(id)
                                    2 -> viewModel.loadGroups(id)
                                    3 -> viewModel.loadMaterials(id)
                                    4 -> viewModel.loadStudentList(id)
                                }
                            }
                        },
                        state = refreshState,
                        indicator = {
                            Indicator(
                                modifier = Modifier.align(Alignment.TopCenter),
                                isRefreshing = isRefreshing.value,
                                state = refreshState,
                                containerColor = MaterialTheme.colorScheme.surface,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    ) {
                        when (it) {
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
                                                    session = it,
                                                    onJoinClick = { id ->
                                                        onJoinSessionClick(id)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                    is ViewState.Error -> {}
                                    is ViewState.Loading -> {
                                        Box(
                                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
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
                                                    meeting = it,
                                                    onJoinClick = { id ->
                                                        onJoinMeetingClick(id)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                    is ViewState.Error -> {}
                                    is ViewState.Loading -> {
                                        Box(
                                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                    is ViewState.Idle -> {}
                                }
                            }
                            2 -> {
                                when (groupsState) {
                                    is ViewState.Success -> {
                                        val classroomAssignments = (groupsState as ViewState.Success).data
                                        LazyColumn(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            items(
                                                items = classroomAssignments,
                                                key = { it -> it.id }
                                            ) {
                                                GroupCard(
                                                    group = it
                                                )
                                            }
                                        }
                                    }
                                    is ViewState.Error -> {}
                                    is ViewState.Loading -> {
                                        Box(
                                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
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
                                    is ViewState.Error -> {}
                                    is ViewState.Loading -> {
                                        Box(
                                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                    is ViewState.Idle -> {}
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
                                        Box(
                                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                    is ViewState.Idle -> {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        is ViewState.Idle -> {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}