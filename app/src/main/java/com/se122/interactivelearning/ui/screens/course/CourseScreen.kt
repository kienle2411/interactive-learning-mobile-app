package com.se122.interactivelearning.ui.screens.course

import android.util.Log
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.ui.components.ClassCard
import com.se122.interactivelearning.ui.components.InputIcon
import com.se122.interactivelearning.ui.components.PrimaryButton
import com.se122.interactivelearning.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(
    viewModel: CourseViewModel = hiltViewModel(),
    onCourseClick: (String) -> Unit
) {
    val classroomList by viewModel.classroomList.collectAsState()
    val classroomCode = remember { mutableStateOf(TextFieldValue("")) }

    val joinState by viewModel.joinClassroomState.collectAsState()

    val showErrorDialog = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }

    LaunchedEffect(joinState) {
        if (joinState is ViewState.Error) {
            errorMessage.value = (joinState as ViewState.Error).message ?: "Unknown error"
            showErrorDialog.value = true
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = "My Course",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                InputIcon(
                    value = classroomCode.value,
                    onValueChange = {
                        classroomCode.value = it
                    },
                    placeholder = "Classroom Code",
                    leadingIcon = Icons.Default.Build,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                PrimaryButton(
                    onClick = {
                        val code = classroomCode.value.text.trim()
                        if (code.isNotEmpty()) {
                            viewModel.joinClassroom(code)
                        }
                    },
                    text = "Join",
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxHeight()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            when (classroomList) {
                is ViewState.Error -> {
                    val msg = (classroomList as ViewState.Error).message ?: "Unknown error"
                    Text(msg, color = androidx.compose.ui.graphics.Color.Red)
                }
                is ViewState.Idle -> {}
                is ViewState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is ViewState.Success -> {
                    val classroomList = (classroomList as ViewState.Success).data
                    val rows = classroomList.chunked(2)
                    LazyColumn {
                        items(
                            items = rows,
                            key = { row -> row.first().classroom.id }
                        ) { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                row.forEach {
                                    Box(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        ClassCard(
                                            classroom = it.classroom,
                                            onClick = {
                                                onCourseClick(it.classroom.id)
                                            }
                                        )
                                    }
                                }
                                if (row.size < 2) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        if (joinState is ViewState.Loading) {
            Dialog(
                onDismissRequest = {}
            ) {
                CircularProgressIndicator()
            }
        }

        if (showErrorDialog.value) {
            BasicAlertDialog(
                onDismissRequest = {
                    showErrorDialog.value = false
                },
                modifier = Modifier
                    .zIndex(1f)
                    .clip(RoundedCornerShape(5.dp))
                    .border(1.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(5.dp))
                    .background(MaterialTheme.colorScheme.background),
                content = {
                    Box {
                        Text(
                            modifier = Modifier.padding(10.dp),
                            text = errorMessage.value,
                        )
                    }
                }
            )
        }
    }
}