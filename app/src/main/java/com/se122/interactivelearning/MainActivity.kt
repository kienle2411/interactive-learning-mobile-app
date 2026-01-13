package com.se122.interactivelearning

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.se122.interactivelearning.ui.components.NavigationBar
import com.se122.interactivelearning.ui.navigation.AppNavGraph
import com.se122.interactivelearning.ui.screens.auth.login.LoginScreen
import com.se122.interactivelearning.ui.theme.InteractiveLearningTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import com.se122.interactivelearning.ui.navigation.NavRoutes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InteractiveLearningTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        MyApplication()
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyApplication() {
    val navController = rememberNavController()
    val viewModel: AppViewModel = hiltViewModel()
    val shouldLogout by viewModel.logoutTrigger.collectAsState()

    val bottomBarRoutes = listOf(
        NavRoutes.HOME,
        NavRoutes.COURSE,
        NavRoutes.PROFILE,
        NavRoutes.NOTIFICATION,
        NavRoutes.QUIZZES
    )

    LaunchedEffect(shouldLogout) {
        if (shouldLogout) {
            navController.navigate("login") {
                popUpTo(0) {
                    inclusive = true
                }
            }
            viewModel.resetLogoutState()
        }
    }

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            if (currentRoute in bottomBarRoutes) {
                NavigationBar(navController = navController, currentRoute = currentRoute)
            }
        },
    ) { innerPadding ->
        AppNavGraph(navController = navController, modifier = Modifier.padding(innerPadding))
    }
}
