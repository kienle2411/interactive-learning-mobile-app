package com.se122.interactivelearning.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.se122.interactivelearning.ui.screens.auth.login.LoginScreen
import com.se122.interactivelearning.ui.screens.auth.register.RegisterScreen
import com.se122.interactivelearning.ui.screens.course.CourseDetailScreen
import com.se122.interactivelearning.ui.screens.course.CourseScreen
import com.se122.interactivelearning.ui.screens.home.HomeScreen
import com.se122.interactivelearning.ui.screens.notification.NotificationScreen
import com.se122.interactivelearning.ui.screens.profile.ProfileScreen

@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier) {

    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME,
        modifier = Modifier.padding(20.dp)
    ) {
        composable(
            route = NavRoutes.HOME
        ) {
            HomeScreen(
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
            ProfileScreen()
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
                }
            )
        }
    }
}