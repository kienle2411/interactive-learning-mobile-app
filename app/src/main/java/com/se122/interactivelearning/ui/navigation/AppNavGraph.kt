package com.se122.interactivelearning.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier?) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME,
        modifier = Modifier.padding(20.dp)
    ) {
        composable(
            route = NavRoutes.HOME
        ) {
            HomeScreen(
                navController = navController,
                onButtonClick = {
                    navController.navigate("login")
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
                },
                onAboutClick = {}
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
    }
}