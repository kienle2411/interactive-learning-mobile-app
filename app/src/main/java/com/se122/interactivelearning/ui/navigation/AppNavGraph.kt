package com.se122.interactivelearning.ui.navigation

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.se122.interactivelearning.ui.screens.auth.login.LoginScreen
import com.se122.interactivelearning.ui.screens.auth.register.RegisterScreen
import com.se122.interactivelearning.ui.screens.course.CourseScreen
import com.se122.interactivelearning.ui.screens.home.HomeScreen
import com.se122.interactivelearning.ui.screens.notification.NotificationScreen
import com.se122.interactivelearning.ui.screens.profile.ProfileScreen

@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME,
    ) {
        composable(
            route = NavRoutes.HOME
        ) {
            HomeScreen()
        }
        composable(
            route = NavRoutes.COURSE
        ) {
            CourseScreen()
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
            LoginScreen()
        }
        composable(
            route = NavRoutes.REGISTER
        ) {
            RegisterScreen()
        }
    }
}