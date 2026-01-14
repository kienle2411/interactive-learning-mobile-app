package com.se122.interactivelearning.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.se122.interactivelearning.R

@Composable
fun NavigationBar(
    navController: NavHostController,
    currentRoute: String?
) {
    Row(
        modifier = Modifier
            .shadow(elevation = 30.dp)
            .fillMaxWidth()
            .height(70.dp)
            .background(color = Color.White),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NavBarList.forEach { item ->
            val isSelected = currentRoute == item.route
            val iconSize = if (isSelected) {
                val size by animateFloatAsState(
                    targetValue = 28f,
                    animationSpec = tween(durationMillis = 180)
                )
                size
            } else {
                22f
            }
            val fontSize = if (isSelected) {
                val size by animateFloatAsState(
                    targetValue = 12f,
                    animationSpec = tween(durationMillis = 180)
                )
                size
            } else {
                10f
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(25.dp))
                    .weight(1f)
                    .fillMaxWidth()
                    .clickable(
                        enabled = !isSelected,
                        onClick = {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
            ) {
                Column(
                    modifier = Modifier.padding(10.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        painter = painterResource(id = if (isSelected) item.iconFilled else item.icon),
                        contentDescription = item.title,
                        modifier = Modifier.size(iconSize.dp)
                    )
                    Text(
                        text = item.title,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = fontSize.sp,
                        fontWeight =  if (isSelected) FontWeight.SemiBold else FontWeight.Normal
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
    NavBarItem("Suggestions", R.drawable.ic_quiz, R.drawable.ic_quiz_fill, "suggestions"),
    NavBarItem("Profile", R.drawable.ic_user, R.drawable.ic_user_fill, "profile")
)
