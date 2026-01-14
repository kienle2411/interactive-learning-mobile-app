package com.se122.interactivelearning.ui.screens.quiz

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.data.remote.dto.QuizAttemptQuestionItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QuizAttemptScreen(
    quizId: String,
    onBackClick: () -> Unit,
    onSubmitted: () -> Unit,
    viewModel: QuizAttemptViewModel = hiltViewModel()
) {
    val quizState by viewModel.quiz.collectAsState()
    val attemptState by viewModel.attempt.collectAsState()
    val questionsState by viewModel.questions.collectAsState()
    val answers by viewModel.answers.collectAsState()
    val submitState by viewModel.submitState.collectAsState()
    val isReadOnly by viewModel.isReadOnly.collectAsState()
    val explanations by viewModel.explanations.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var explanationQuestionId by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(quizId) {
        viewModel.loadQuiz(quizId)
        viewModel.loadAttempt(quizId)
    }

    LaunchedEffect(submitState) {
        if (submitState is ViewState.Success) {
            viewModel.resetSubmitState()
        }
    }

    val questions = when (questionsState) {
        is ViewState.Success -> (questionsState as ViewState.Success).data.sortedBy { it.orderIndex }
        else -> emptyList()
    }
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { questions.size.coerceAtLeast(1) }
    )

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onBackClick() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = (quizState as? ViewState.Success)?.data?.title ?: "Quiz",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        },
        bottomBar = {
            if (!isReadOnly && questions.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(questions) { index, _ ->
                            val isSelected = pagerState.currentPage == index
                            val bgColor = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                            val textColor = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
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
                                    },
                                contentAlignment = Alignment.Center
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
                    Button(
                        onClick = { viewModel.submitQuiz() },
                        enabled = submitState !is ViewState.Loading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val buttonText = when (submitState) {
                            is ViewState.Loading -> "Submitting..."
                            else -> "Submit Quiz"
                        }
                        Text(text = buttonText)
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
        }
    ) { innerPadding ->
        when (questionsState) {
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
                } else if (isReadOnly) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val attempt = (attemptState as? ViewState.Success)?.data
                        if (attempt != null) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Quiz Result",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Total score: ${attempt.score ?: 0}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                        QuizHistory(
                            questions = questions,
                            modifier = Modifier.fillMaxSize(),
                            onExplain = { questionId, studentAnswer ->
                                explanationQuestionId = questionId
                                viewModel.explainQuestion(questionId, studentAnswer)
                            }
                        )
                    }
                } else {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) { page ->
                        val item = questions[page]
                        QuizQuestionPage(
                            index = page,
                            item = item,
                            answerDraft = answers[item.question.id],
                            onOptionSelected = { optionId ->
                                viewModel.setOptionAnswer(item.question.id, optionId)
                            },
                            onTextChange = { text ->
                                viewModel.setTextAnswer(item.question.id, text)
                            }
                        )
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
                        text = (questionsState as ViewState.Error).message ?: "Failed to load"
                    )
                }
            }

            else -> {}
        }
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
                Button(onClick = { explanationQuestionId = null }) {
                    Text(text = "Close")
                }
            }
        )
    }
}

@Composable
private fun QuizHistory(
    questions: List<QuizAttemptQuestionItem>,
    modifier: Modifier = Modifier,
    onExplain: (String, String?) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(questions) { index, item ->
            QuizAnswerCard(
                index = index,
                points = item.points,
                question = item.question.content ?: "Untitled question",
                item = item,
                onExplain = onExplain
            )
        }
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun QuizAnswerCard(
    index: Int,
    points: Int,
    question: String,
    item: QuizAttemptQuestionItem,
    onExplain: (String, String?) -> Unit
) {
    val answer = item.answer
    val hasGrade = (answer?.score != null) || !answer?.feedback.isNullOrBlank()
    val isEssay = item.question.type != "MCQ"
    val correctnessText = when {
        answer == null -> "Not graded"
        answer.isCorrect == true -> "Correct"
        answer.isCorrect == false -> "Incorrect"
        isEssay && hasGrade -> "Đã chấm (AI)"
        else -> "Not graded"
    }
    val correctnessColor = when {
        answer?.isCorrect == true -> Color(0xFF147D64)
        answer?.isCorrect == false -> Color(0xFF9F1D1D)
        isEssay && hasGrade -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val scoreText = answer?.score?.toString() ?: "-"
    val answerText = when {
        answer == null -> "No answer"
        answer.textAnswer?.isNotBlank() == true -> answer.textAnswer
        else -> {
            val option = item.question.options.firstOrNull { it.id == answer.selectedOptionId }
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
                    text = "Score: $scoreText/$points",
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
                    value = answer.textAnswer ?: "",
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
                    item.question.options.forEach { option ->
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
                onClick = { onExplain(item.question.id, answerText.takeIf { it != "No answer" }) },
                enabled = item.question.id.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Explain with AI")
            }
        }
    }
}

@Composable
private fun QuizQuestionPage(
    index: Int,
    item: QuizAttemptQuestionItem,
    answerDraft: QuizAnswerDraft?,
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RadioButton(
                            selected = answerDraft?.selectedOptionId == option.id,
                            onClick = { onOptionSelected(option.id) }
                        )
                        Text(
                            text = option.content,
                            style = MaterialTheme.typography.bodyMedium
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
