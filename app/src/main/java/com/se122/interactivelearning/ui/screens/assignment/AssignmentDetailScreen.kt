package com.se122.interactivelearning.ui.screens.assignment

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.data.remote.dto.AssignmentQuestionItem
import com.se122.interactivelearning.data.remote.dto.AssignmentType
import com.se122.interactivelearning.data.remote.dto.AssignmentAnswerResponse
import com.se122.interactivelearning.data.remote.dto.SubmissionResponse
import com.se122.interactivelearning.ui.components.chat.AgentChatFloating
import com.se122.interactivelearning.ui.components.chat.AgentChatViewModel
import com.se122.interactivelearning.ui.components.chat.ChatContext
import com.se122.interactivelearning.ui.components.chat.ChatScopeType
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AssignmentDetailScreen(
    assignmentId: String,
    onBackClick: () -> Unit,
    viewModel: AssignmentDetailViewModel = hiltViewModel(),
    chatViewModel: AgentChatViewModel = hiltViewModel()
) {
    val assignmentState by viewModel.assignment.collectAsState()
    val answers by viewModel.answers.collectAsState()
    val submitState by viewModel.submitState.collectAsState()
    val submissions by viewModel.submissions.collectAsState()
    val explanations by viewModel.explanations.collectAsState()
    val chatState by chatViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    var showChat by rememberSaveable { mutableStateOf(false) }
    var explanationQuestionId by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedFiles = remember { mutableStateOf<List<Uri>>(emptyList()) }
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            selectedFiles.value = uris
        }

    }

    LaunchedEffect(assignmentId) {
        viewModel.loadAssignmentQuestions(assignmentId)
        viewModel.loadSubmissions(assignmentId)
    }

    LaunchedEffect(assignmentId, assignmentState) {
        val assignment = (assignmentState as? ViewState.Success)?.data
        val title = assignment?.title?.let { "Trợ lý bài tập: $it" } ?: "Trợ lý bài tập"
        chatViewModel.setContext(
            ChatContext(
                scopeType = ChatScopeType.ASSIGNMENT,
                classroomId = assignment?.classroomId,
                assignmentId = assignment?.id,
                title = title
            )
        )
    }

    val assignmentType = (assignmentState as? ViewState.Success)?.data?.type
    val questions = when (assignmentState) {
        is ViewState.Success -> {
            (assignmentState as ViewState.Success).data.assignmentQuestions.sortedBy { it.orderIndex }
        }

        else -> emptyList()
    }
    val latestSubmission = (submissions as? ViewState.Success)?.data?.firstOrNull()
    val isReadOnly =
        latestSubmission?.status == com.se122.interactivelearning.data.remote.dto.SubmissionStatus.SUBMITTED ||
            latestSubmission?.status == com.se122.interactivelearning.data.remote.dto.SubmissionStatus.GRADED

    BackHandler {
        if (isReadOnly) {
            onBackClick()
        } else {
            showExitDialog = true
        }
    }
    val pagerState = rememberPagerState(
        initialPage = 0, pageCount = { questions.size.coerceAtLeast(1) })

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(text = "Thoat bai lam?") },
            text = { Text(text = "Neu thoat, du lieu hien tai se bi mat.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        onBackClick()
                    }) {
                    Text(text = "Thoat")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(text = "O lai")
                }
            })
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showExitDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    Text(
                        text = when (assignmentState) {
                            is ViewState.Success -> (assignmentState as ViewState.Success).data.title
                            else -> "Assignment"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (!isReadOnly && questions.isNotEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Question ${pagerState.currentPage + 1}/${questions.size}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }, bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SubmissionSummary(
                    submissionsState = submissions, onOpenFile = { url ->
                        val intent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url)
                        )
                        context.startActivity(intent)
                    })
                if (!isReadOnly && assignmentType == AssignmentType.ASSIGNMENT) {
                    AttachedFilesSection(
                        files = selectedFiles.value,
                        onPickFiles = { filePicker.launch(arrayOf("*/*")) },
                        onClear = { selectedFiles.value = emptyList() })
                }
                if (!isReadOnly && questions.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier.height(40.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(questions) { index, item ->
                            val answered = answers[item.question.id]?.let {
                                it.selectedOptionId != null || !it.textAnswer.isNullOrBlank()
                            } == true
                            val isSelected = index == pagerState.currentPage
                            val bgColor = when {
                                isSelected -> MaterialTheme.colorScheme.primary
                                answered -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                            val textColor = when {
                                isSelected -> MaterialTheme.colorScheme.onPrimary
                                answered -> MaterialTheme.colorScheme.onPrimaryContainer
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(bgColor)
                                    .clickable {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(index)
                                        }
                                    }, contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (index + 1).toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = textColor
                                )
                            }
                        }
                    }
                }
                if (!isReadOnly) {
                    Button(
                        onClick = { viewModel.submitAssignment(assignmentId, selectedFiles.value) },
                        enabled = submitState !is ViewState.Loading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val buttonText = when {
                            submitState is ViewState.Loading -> "Submitting..."
                            else -> "Submit Assignment"
                        }
                        Text(text = buttonText)
                    }
                    if (submitState is ViewState.Success) {
                        Text(
                            text = "Submitted successfully",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                    if (submitState is ViewState.Error) {
                        Text(
                            text = (submitState as ViewState.Error).message ?: "Submit failed",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Red,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }) { innerPadding ->
            when (assignmentState) {
                is ViewState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is ViewState.Success -> {
                    if (questions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No questions yet")
                        }
                    } else if (isReadOnly && latestSubmission != null) {
                        SubmissionHistory(
                            questions = questions,
                            submission = latestSubmission,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            onExplain = { questionId, studentAnswer ->
                                explanationQuestionId = questionId
                                viewModel.explainQuestion(questionId, studentAnswer)
                            }
                        )
                    } else {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) { page ->
                            AssignmentQuestionPage(
                                index = page,
                                item = questions[page],
                                answerDraft = answers[questions[page].question.id],
                                onOptionSelected = { optionId ->
                                    viewModel.setOptionAnswer(questions[page].question.id, optionId)
                                },
                                onTextChange = { text ->
                                    viewModel.setTextAnswer(questions[page].question.id, text)
                                })
                        }
                    }
                }

                is ViewState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (assignmentState as ViewState.Error).message ?: "Failed to load"
                        )
                    }
                }

                else -> {}
            }

            val explanationState = explanationQuestionId?.let { explanations[it] }
            if (explanationQuestionId != null) {
                AlertDialog(
                    onDismissRequest = { explanationQuestionId = null },
                    title = { Text(text = "AI Explanation") },
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 420.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            when (explanationState) {
                                is ViewState.Loading -> {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(18.dp))
                                        Text(text = "Generating explanation...")
                                    }
                                }
                                is ViewState.Success -> {
                                    Text(text = explanationState.data.explanation)
                                    val citations = explanationState.data.citations.orEmpty()
                                    if (citations.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Sources",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            citations.forEach { citation ->
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(
                                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                                            shape = RoundedCornerShape(10.dp)
                                                        )
                                                        .padding(10.dp)
                                                ) {
                                                    Text(
                                                        text = "${citation.documentName} (${citation.documentId})",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                    citation.lesson?.let { lesson ->
                                                        Text(
                                                            text = "Lesson: ${lesson.title}",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                    citation.topic?.let { topic ->
                                                        Text(
                                                            text = "Topic: ${topic.name}",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                is ViewState.Error -> Text(
                                    text = explanationState.message ?: "Failed to generate explanation"
                                )
                                else -> Text(text = "Generating explanation...")
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { explanationQuestionId = null }) {
                            Text(text = "Close")
                        }
                    }
                )
            }

            if (isReadOnly) {
                AgentChatFloating(
                    isOpen = showChat,
                    title = "Trợ lý bài tập",
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
    }
}

@Composable
private fun AssignmentQuestionPage(
    index: Int,
    item: AssignmentQuestionItem,
    answerDraft: AssignmentAnswerDraft?,
    onOptionSelected: (String) -> Unit,
    onTextChange: (String) -> Unit
) {
    val question = item.question
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Question ${index + 1}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = question.content ?: "Untitled question",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${item.points} points",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        if (question.type == "MCQ") {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                question.options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { onOptionSelected(option.id) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        RadioButton(
                            selected = answerDraft?.selectedOptionId == option.id,
                            onClick = { onOptionSelected(option.id) })
                        Text(
                            text = option.content, style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else {
            TextField(
                value = answerDraft?.textAnswer ?: "",
                onValueChange = onTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                placeholder = { Text("Type your answer...") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
private fun SubmissionHistory(
    questions: List<AssignmentQuestionItem>,
    submission: SubmissionResponse,
    modifier: Modifier = Modifier,
    onExplain: (String, String?) -> Unit
) {
    val answerMap = submission.answers?.associateBy { it.questionId } ?: emptyMap()
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(questions) { index, item ->
            val answer = answerMap[item.question.id]
            SubmissionAnswerCard(
                index = index,
                points = item.points,
                questionId = item.question.id,
                question = item.question.content ?: "Untitled question",
                answer = answer,
                onExplain = onExplain
            )
        }
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SubmissionAnswerCard(
    index: Int,
    points: Int,
    questionId: String,
    question: String,
    answer: AssignmentAnswerResponse?,
    onExplain: (String, String?) -> Unit
) {
    val correctnessText = when (answer?.isCorrect) {
        true -> "Correct"
        false -> "Incorrect"
        null -> "Not graded"
    }
    val correctnessColor = when (answer?.isCorrect) {
        true -> Color(0xFF147D64)
        false -> Color(0xFF9F1D1D)
        null -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val scoreText = answer?.score?.toString() ?: "-"
    val isEssay = answer?.question?.type != "MCQ"
    val answerText = when {
        answer == null -> "No answer"
        answer.textAnswer?.isNotBlank() == true -> answer.textAnswer
        else -> {
            val option = answer.question.options.firstOrNull { it.id == answer.selectedOptionId }
            option?.content ?: "No answer"
        }
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Question ${index + 1}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = question,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Answer",
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(999.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Result",
                    modifier = Modifier
                        .background(
                            color = correctnessColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(999.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = correctnessColor
                )
                Text(
                    text = "Score: $scoreText/${points}",
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(999.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            if (answer == null) {
                Text(
                    text = "No answer",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (isEssay) {
                TextField(
                    value = answerText,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    answer.question.options.forEach { option ->
                        val isSelected = option.id == answer.selectedOptionId
                        val isCorrect = option.isCorrect
                        val optionBg = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                        val optionText = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                        val optionBorderColor = if (isCorrect) {
                            Color(0xFF147D64)
                        } else {
                            Color.Transparent
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(2.dp, optionBorderColor, RoundedCornerShape(12.dp))
                                .background(optionBg)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = option.content,
                                style = MaterialTheme.typography.bodyMedium,
                                color = optionText,
                                modifier = Modifier.weight(1f)
                            )
                            if (isSelected) {
                                Text(
                                    text = "Selected",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = optionText
                                )
                            }
                        }
                    }
                }
            }

            Text(
                text = "Result: $correctnessText",
                style = MaterialTheme.typography.bodyMedium,
                color = correctnessColor
            )
            Text(
                text = "Feedback",
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(999.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = answer?.feedback ?: "No feedback",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = { onExplain(questionId, answerText.takeIf { it != "No answer" }) },
                enabled = questionId.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Explain with AI")
            }
        }
    }
}

@Composable
private fun SubmissionSummary(
    submissionsState: ViewState<List<SubmissionResponse>>, onOpenFile: (String) -> Unit
) {
    if (submissionsState is ViewState.Success && submissionsState.data.isNotEmpty()) {
        val latest = submissionsState.data.first()
        Card(
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Last submission",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Status: ${latest.status}",
                    style = MaterialTheme.typography.bodySmall
                )
                latest.submittedAt?.let {
                    Text(
                        text = "Submitted at: $it",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                val submissionFiles = latest.submissionFile ?: emptyList()
                if (submissionFiles.isNotEmpty()) {
                    Text(
                        text = "Files",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        submissionFiles.forEach { file ->
                            Text(
                                text = file.file.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { onOpenFile(file.file.url) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AttachedFilesSection(
    files: List<Uri>, onPickFiles: () -> Unit, onClear: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Attach files",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onPickFiles) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = "Pick files"
                        )
                    }
                    if (files.isNotEmpty()) {
                        TextButton(onClick = onClear) {
                            Text(text = "Clear")
                        }
                    }
                }
            }
            if (files.isEmpty()) {
                Text(
                    text = "No files selected",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                val context = LocalContext.current
                files.forEach { uri ->
                    val name = runCatching {
                        context.contentResolver.query(uri, null, null, null, null)
                            ?.use { cursor ->
                                val nameIndex =
                                    cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                                if (cursor.moveToFirst() && nameIndex >= 0) cursor.getString(
                                    nameIndex
                                ) else uri.lastPathSegment
                            }
                    }.getOrNull() ?: uri.lastPathSegment ?: "file"
                    Text(
                        text = name, style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
