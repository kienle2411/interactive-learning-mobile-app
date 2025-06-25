package com.se122.interactivelearning.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.se122.interactivelearning.ui.screens.auth.login.LoginScreen
import com.se122.interactivelearning.ui.screens.auth.register.RegisterScreen
import com.se122.interactivelearning.ui.screens.course.CourseDetailScreen
import com.se122.interactivelearning.ui.screens.course.CourseScreen
import com.se122.interactivelearning.ui.screens.home.HomeScreen
import com.se122.interactivelearning.ui.screens.meeting.InMeetingScreen
import com.se122.interactivelearning.ui.screens.meeting.MeetingJoinScreen
import com.se122.interactivelearning.ui.screens.meeting.MeetingSharedViewModel
import com.se122.interactivelearning.ui.screens.notification.NotificationScreen
import com.se122.interactivelearning.ui.screens.profile.EditProfileScreen
import com.se122.interactivelearning.ui.screens.profile.ProfileScreen
import com.se122.interactivelearning.ui.screens.quiz.InQuizScreen
import com.se122.interactivelearning.ui.screens.quiz.QuizJoinScreen
import com.se122.interactivelearning.ui.screens.quiz.QuizListScreen
import com.se122.interactivelearning.ui.screens.session.InSessionScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier?) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME
    ) {
        composable(
            route = NavRoutes.HOME
        ) {
            HomeScreen(
                onQuizzesClick = {
                    navController.navigate("quizzes")
                },
                onJoinMeetingClick = {
                    navController.navigate("meeting_join/${it}")
                },
                onJoinSessionClick = { sessionId ->
                    navController.navigate("in_session/${sessionId}")
                }
            )
        }
        composable(
            route = NavRoutes.COURSE
        ) {
            CourseScreen(
                onCourseClick = { courseId ->
                    navController.navigate("course_detail/${courseId}")
                }
            )
        }
        composable(
            route = NavRoutes.NOTIFICATION
        ) {
            NotificationScreen()
        }
        composable(
            route = NavRoutes.PROFILE
        ) {
            ProfileScreen(
                onEditProfileClick = {
                    navController.navigate("edit_profile")
                },
                onSettingsClick = {
                    navController.navigate("settings")
                }
            )
        }
        composable(
            route = NavRoutes.EDIT_PROFILE
        ) {
            EditProfileScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = NavRoutes.LOGIN
        ) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(
            route = NavRoutes.REGISTER
        ) {
            RegisterScreen()
        }
        composable(
            route = NavRoutes.COURSE_DETAIL
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("id")
            CourseDetailScreen(
                id = courseId.toString(),
                onBackClick = {
                    navController.popBackStack()
                },
                onJoinMeetingClick = { meetingId ->
                    navController.navigate("meeting_join/${meetingId}")
                },
                onJoinSessionClick = { sessionId ->
                    navController.navigate("in_session/${sessionId}")
                }
            )
        }
        navigation(startDestination = NavRoutes.MEETING_JOIN, route = "meeting_graph") {
            composable(
                route = NavRoutes.IN_MEETING
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("meeting_graph")
                }
                val sharedViewModel: MeetingSharedViewModel = hiltViewModel(parentEntry)
                val meetingId = backStackEntry.arguments?.getString("id")
                InMeetingScreen(
                    id = meetingId.toString(),
                    onEndCallClick = {
                        navController.navigate("home")
                    },
                    meetingSharedViewModel = sharedViewModel
                )
            }
            composable(
                route = NavRoutes.MEETING_JOIN
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("meeting_graph")
                }
                val sharedViewModel: MeetingSharedViewModel = hiltViewModel(parentEntry)
                val meetingId = backStackEntry.arguments?.getString("id")
                MeetingJoinScreen(
                    id = meetingId.toString(),
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onJoinClick = { meetingId ->
                        navController.navigate("in_meeting/${meetingId}")
                    },
                    meetingSharedViewModel = sharedViewModel
                )
            }
        }
        composable(
            route = NavRoutes.IN_SESSION
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("id")
            InSessionScreen(
                sessionId = sessionId.toString(),
                onLeaveClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = NavRoutes.QUIZ_JOIN
        ) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("id")
            QuizJoinScreen(
                quizId = quizId.toString(),
                onJoinClick = {},
                onBackClick = {},
                onStart = {
                    navController.navigate("in_quiz/${quizId}")
                }
            )
        }
        composable(
            route = NavRoutes.IN_QUIZ
        ) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("id")
            InQuizScreen(
                quizId = quizId.toString(),
                onBackClick = {}
            )
        }
        composable(
            route = NavRoutes.QUIZZES
        ) {
            QuizListScreen(
                onJoin = {
                    navController.navigate("quiz_join/${it}")
                }
            )
        }
    }
}