package com.se122.interactivelearning.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.se122.interactivelearning.R

@Composable
fun NavigationBar(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Row(
        modifier = Modifier
            .shadow(elevation = 10.dp)
            .fillMaxWidth()
            .height(70.dp)
            .background(color = MaterialTheme.colorScheme.surface),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NavBarList.forEach { item ->
            val iconSize by animateFloatAsState(
                targetValue = if (currentRoute == item.route) 28f else 22f,
                animationSpec = if (currentRoute == item.route) tween(durationMillis = 300) else tween(durationMillis = 0)
            )
            val fontSize by animateFloatAsState(
                targetValue = if (currentRoute == item.route) 12f else 10f,
                animationSpec = if (currentRoute == item.route) tween(durationMillis = 300) else tween(durationMillis = 0)
            )
            Box(
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        painter = painterResource(id = if (currentRoute == item.route) item.iconFilled else item.icon),
                        contentDescription = item.title,
                        modifier = Modifier.size(iconSize.dp)
                    )
                    Text(
                        text = item.title,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = fontSize.sp,
                        fontWeight =  if (currentRoute == item.route) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

data class NavBarItem(
    val title: String,
    val icon: Int,
    val iconFilled: Int,
    val route: String
)

val NavBarList = listOf(
    NavBarItem("Home", R.drawable.ic_home, R.drawable.ic_home_fill, "home"),
    NavBarItem("Course", R.drawable.ic_course, R.drawable.ic_course_fill, "course"),
    NavBarItem("Notification", R.drawable.ic_notification, R.drawable.ic_notification_fill, "notification"),
    NavBarItem("Profile", R.drawable.ic_user, R.drawable.ic_user_fill, "profile")
)