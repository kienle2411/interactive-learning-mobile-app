package com.se122.interactivelearning.ui.screens.course

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.ui.theme.GraySecondary
import com.se122.interactivelearning.ui.theme.GreenPrimary
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.compose.ui.res.painterResource
import com.se122.interactivelearning.R
import com.se122.interactivelearning.ui.components.AssignmentCard
import com.se122.interactivelearning.ui.components.MaterialCard
import com.se122.interactivelearning.ui.components.SessionCard
import com.se122.interactivelearning.ui.components.StudentCard
import com.se122.interactivelearning.ui.components.chat.AgentChatFloating
import com.se122.interactivelearning.ui.components.chat.AgentChatViewModel
import com.se122.interactivelearning.ui.components.chat.ChatContext
import com.se122.interactivelearning.ui.components.chat.ChatScopeType
import com.se122.interactivelearning.data.remote.dto.SuggestedActionResponse
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    id: String,
    viewModel: CourseDetailViewModel = hiltViewModel(),
    chatViewModel: AgentChatViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onJoinSessionClick: (String) -> Unit,
    onLessonClick: (String) -> Unit,
    onAssignmentClick: (String) -> Unit,
    onSuggestionClick: (SuggestedActionResponse) -> Unit
) {
    val classroomDetails by viewModel.classroomDetails.collectAsState()
    val classroomStudents by viewModel.classroomStudents.collectAsState()
    val classroomMaterials by viewModel.classroomMaterials.collectAsState()
    val classroomSessions by viewModel.classroomSessions.collectAsState()
    val fileDownloadLink by viewModel.fileDownloadLink.collectAsState()
    val classroomAssignments by viewModel.classroomAssignments.collectAsState()
    val classroomTopics by viewModel.classroomTopics.collectAsState()
    val topicLessons by viewModel.topicLessons.collectAsState()
    val classroomSuggestions by viewModel.classroomSuggestions.collectAsState()
    val chatState by chatViewModel.uiState.collectAsState()
    var showChat by rememberSaveable { mutableStateOf(false) }


    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    val tabs = listOf("Session", "Assignment", "Lesson", "Material", "Information")
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
    val listStates = List(tabs.size) { rememberLazyListState() }
    val expandedTopics = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(id) {
        viewModel.loadCourseDetails(id)
        viewModel.loadStudentList(id)
        viewModel.loadMaterials(id)
        viewModel.loadSessions(id)
        viewModel.loadAssignments(id)
        viewModel.loadTopics(id)
        viewModel.loadClassroomSuggestions(id)
    }

    LaunchedEffect(id, classroomDetails) {
        val title = if (classroomDetails is ViewState.Success) {
            "Trợ lý: ${(classroomDetails as ViewState.Success).data.name}"
        } else {
            "Trợ lý lớp học"
        }
        chatViewModel.setContext(
            ChatContext(
                scopeType = ChatScopeType.CLASSROOM,
                classroomId = id,
                title = title
            )
        )
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

    LaunchedEffect(classroomAssignments) {
        if (pagerState.currentPage == 1) {
            when (classroomAssignments) {
                is ViewState.Success, is ViewState.Error -> refreshingStates[1].value = false
                else -> {}
            }
        }
    }

    LaunchedEffect(classroomTopics) {
        if (pagerState.currentPage == 2) {
            when (classroomTopics) {
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
            Text(msg, color = Color.Red)
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
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize().padding(20.dp)
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        tabs.forEachIndexed { index, title ->
                            val isSelected = pagerState.currentPage == index
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSelected) GreenPrimary else GraySecondary)
                                    .clickable {
                                        coroutineScope.launch {
                                            pagerState.scrollToPage(index)
                                        }
                                    }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = title,
                                    color = if (isSelected) Color.White else Color.Black,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
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
                                        1 -> viewModel.loadAssignments(id)
                                        2 -> viewModel.loadTopics(id)
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
                                            if (classroomSessions.isEmpty()) {
                                                Box(
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "No sessions",
                                                        style = MaterialTheme.typography.titleMedium
                                                    )
                                                }
                                            } else {
                                                LazyColumn(
                                                    state = listStates[it],
                                                    modifier = Modifier.fillMaxSize(),
                                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                                ) {
                                                    items(
                                                        items = classroomSessions,
                                                        key = { item -> item.id }
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
                                    when (classroomAssignments) {
                                        is ViewState.Success -> {
                                            val classroomAssignments = (classroomAssignments as ViewState.Success).data
                                            if (classroomAssignments.isEmpty()) {
                                                Box(
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "No assignments",
                                                        style = MaterialTheme.typography.titleMedium
                                                    )
                                                }
                                            } else {
                                                LazyColumn(
                                                    state = listStates[it],
                                                    modifier = Modifier.fillMaxSize(),
                                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                                ) {
                                                    items(
                                                        items = classroomAssignments,
                                                        key = { item -> item.id }
                                                    ) {
                                                        AssignmentCard(
                                                            assignment = it,
                                                            onClick = { assignment ->
                                                                onAssignmentClick(assignment.id)
                                                            }
                                                        )
                                                    }
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
                                    when (classroomTopics) {
                                        is ViewState.Success -> {
                                            val classroomTopics = (classroomTopics as ViewState.Success).data
                                            if (classroomTopics.isEmpty()) {
                                                Box(
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "No topics",
                                                        style = MaterialTheme.typography.titleMedium
                                                    )
                                                }
                                            } else {
                                                LazyColumn(
                                                    state = listStates[it],
                                                    modifier = Modifier.fillMaxSize(),
                                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                                ) {
                                                    items(
                                                        items = classroomTopics,
                                                        key = { item -> item.id }
                                                    ) { topic ->
                                                        val isExpanded = expandedTopics[topic.id] == true
                                                        val lessonState = topicLessons[topic.id]
                                                        Card(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            shape = RoundedCornerShape(12.dp),
                                                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                                        ) {
                                                            Column(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .clickable {
                                                                        if (!isExpanded && (lessonState == null || lessonState is ViewState.Error)) {
                                                                            viewModel.loadLessonsForTopic(topic.id)
                                                                        }
                                                                        expandedTopics[topic.id] = !isExpanded
                                                                    }
                                                                    .padding(12.dp)
                                                            ) {
                                                                Row(
                                                                    modifier = Modifier.fillMaxWidth(),
                                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                                    verticalAlignment = Alignment.CenterVertically
                                                                ) {
                                                                    Column(
                                                                        modifier = Modifier.weight(1f)
                                                                    ) {
                                                                        Text(
                                                                            text = topic.name,
                                                                            style = MaterialTheme.typography.titleMedium
                                                                        )
                                                                        if (!topic.description.isNullOrBlank()) {
                                                                            Text(
                                                                                text = topic.description ?: "",
                                                                                style = MaterialTheme.typography.bodySmall,
                                                                                color = GraySecondary
                                                                            )
                                                                        }
                                                                    }
                                                                    Icon(
                                                                        imageVector = if (isExpanded) {
                                                                            Icons.Default.KeyboardArrowUp
                                                                        } else {
                                                                            Icons.Default.KeyboardArrowDown
                                                                        },
                                                                        contentDescription = "Toggle"
                                                                    )
                                                                }
                                                                if (isExpanded) {
                                                                    when (lessonState) {
                                                                        is ViewState.Success -> {
                                                                            val lessons = lessonState.data
                                                                            if (lessons.isEmpty()) {
                                                                                Text(
                                                                                    text = "No lessons",
                                                                                    style = MaterialTheme.typography.bodySmall,
                                                                                    color = GraySecondary,
                                                                                    modifier = Modifier.padding(top = 8.dp)
                                                                                )
                                                                            } else {
                                                                                Column(
                                                                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                                                                    modifier = Modifier.padding(top = 8.dp)
                                                                                ) {
                                                                                    lessons.forEach { lesson ->
                                                                                        Card(
                                                                                            modifier = Modifier
                                                                                                .fillMaxWidth(),
                                                                                            shape = RoundedCornerShape(10.dp),
                                                                                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                                                                            onClick = { onLessonClick(lesson.id) }
                                                                                        ) {
                                                                                            Text(
                                                                                                text = lesson.title,
                                                                                                style = MaterialTheme.typography.bodySmall,
                                                                                                modifier = Modifier.padding(10.dp)
                                                                                            )
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        is ViewState.Error -> {
                                                                            Text(
                                                                                text = lessonState.message ?: "Unknown error",
                                                                                style = MaterialTheme.typography.bodySmall,
                                                                                color = Color.Red,
                                                                                modifier = Modifier.padding(top = 8.dp)
                                                                            )
                                                                        }
                                                                        is ViewState.Loading -> {
                                                                            Box(
                                                                                modifier = Modifier
                                                                                    .fillMaxWidth()
                                                                                    .padding(top = 8.dp),
                                                                                contentAlignment = Alignment.CenterStart
                                                                            ) {
                                                                                CircularProgressIndicator(
                                                                                    modifier = Modifier.size(18.dp)
                                                                                )
                                                                            }
                                                                        }
                                                                        else -> {}
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
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
                                            if (classroomMaterials.isEmpty()) {
                                                Box(
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "No materials",
                                                        style = MaterialTheme.typography.titleMedium
                                                    )
                                                }
                                            } else {
                                                LazyColumn(
                                                    state = listStates[it],
                                                    modifier = Modifier.fillMaxSize(),
                                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                                ) {
                                                    items(
                                                        items = classroomMaterials,
                                                        key = { item -> item.id }
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
                                            var membersExpanded by rememberSaveable { mutableStateOf(false) }
                                            val sortedStudents = classroomStudents.sortedByDescending { it.score ?: 0 }
                                            LazyColumn(
                                                state = listStates[it],
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(horizontal = 5.dp),
                                                verticalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                item {
                                                    Text(
                                                        text = classroomDetails.name,
                                                        style = MaterialTheme.typography.titleMedium
                                                    )
                                                }
                                                item {
                                                    Text(
                                                        text = classroomDetails.description ?: "",
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }
                                                item {
                                                    HorizontalDivider()
                                                }
                                                item {
                                                    ClassroomSuggestionsSection(
                                                        suggestionsState = classroomSuggestions,
                                                        onSuggestionClick = onSuggestionClick
                                                    )
                                                }
                                                item {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clickable {
                                                                membersExpanded = !membersExpanded
                                                            },
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = "Participants",
                                                            style = MaterialTheme.typography.titleMedium
                                                        )
                                                        Icon(
                                                            imageVector = if (membersExpanded) {
                                                                Icons.Default.KeyboardArrowUp
                                                            } else {
                                                                Icons.Default.KeyboardArrowDown
                                                            },
                                                            contentDescription = "Toggle members"
                                                        )
                                                    }
                                                }
                                                if (membersExpanded) {
                                                    items(
                                                        items = sortedStudents,
                                                        key = { item -> item.id }
                                                    ) {
                                                        StudentCard(
                                                            student = it.student,
                                                            score = it.score
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        is ViewState.Error -> {
                                            val msg = (classroomStudents as ViewState.Error).message ?: "Unknown error"
                                            Text(msg, color = Color.Red)
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

                AgentChatFloating(
                    isOpen = showChat,
                    title = "Trợ lý lớp học",
                    messages = chatState.messages,
                    isSending = chatState.isSending,
                    isLoading = chatState.isLoading,
                    errorMessage = chatState.errorMessage,
                    onToggle = {
                        showChat = !showChat
                        if (showChat) {
                            chatViewModel.ensureThread()
                        }
                    },
                    onSend = { chatViewModel.sendMessage(it) },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                )
            }
        }
        is ViewState.Idle -> {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun ClassroomSuggestionsSection(
    suggestionsState: ViewState<com.se122.interactivelearning.data.remote.dto.SuggestionsResponse>,
    onSuggestionClick: (SuggestedActionResponse) -> Unit
) {
    when (suggestionsState) {
        is ViewState.Loading -> {
            Text(
                text = "Suggestions",
                style = MaterialTheme.typography.titleMedium
            )
            CircularProgressIndicator(modifier = Modifier.size(18.dp))
        }
        is ViewState.Success -> {
            val suggestions = suggestionsState.data.suggestions
            if (suggestions.isNotEmpty()) {
                Text(
                    text = "Suggestions",
                    style = MaterialTheme.typography.titleMedium
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    suggestions.forEach { suggestion ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSuggestionClick(suggestion) },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column {
                                if (suggestion.type == "LESSON") {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_lesson_placeholder),
                                        contentDescription = "Lesson placeholder",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = suggestion.title,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    suggestion.subtitle?.let { subtitle ->
                                        Text(
                                            text = subtitle,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = GraySecondary
                                        )
                                    }
                                    Text(
                                        text = suggestion.reason,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
                HorizontalDivider()
            }
        }
        else -> {}
    }
}
