package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.HabitWithStats
import com.example.ui.components.HabitCard
import com.example.ui.components.SparkFlameIcon
import com.example.ui.theme.SparkCanvas
import com.example.ui.theme.SparkOrange
import java.time.LocalTime

@Composable
fun HomeScreen(
    userName: String,
    habits: List<HabitWithStats>,
    onAvatarClick: () -> Unit,
    onCardClick: (HabitWithStats) -> Unit,
    onToggleComplete: (Long) -> Unit,
    onAddHabitClick: () -> Unit,
    onWidgetOptionsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val greetingTime = when (LocalTime.now().hour) {
        in 5..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SparkCanvas)
    ) {
        // Gradient background header backdrop
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFF8329),
                            Color(0xFFFF5C1C),
                            Color(0xFFFF4800)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User Avatar
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White.copy(alpha = 0.85f), CircleShape)
                        .clickable(onClick = onAvatarClick)
                        .testTag("home_avatar_button")
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.avatar_user),
                        contentDescription = "User Profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Action Icons (Widgets, Quick Streak & Menu)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onWidgetOptionsClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.22f))
                            .testTag("home_widget_options_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Widgets,
                            contentDescription = "Widget Options",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onAddHabitClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.22f))
                    ) {
                        SparkFlameIcon(size = 18.dp, tint = Color.White)
                    }

                    IconButton(
                        onClick = onAvatarClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.22f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Menu",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Greeting & Subtitle banner
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "$greetingTime ${userName.split(" ").firstOrNull() ?: userName}",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Keep the streak alive,\nspark your daily motivation.",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 28.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Habit Cards List
            if (habits.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        SparkFlameIcon(size = 48.dp, tint = SparkOrange)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No habits tracked yet",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E130D)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap below to start your first daily streak!",
                            fontSize = 14.sp,
                            color = Color(0xFF827067)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onAddHabitClick,
                            colors = ButtonDefaults.buttonColors(containerColor = SparkOrange),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text("Create a Streak", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(habits, key = { it.habit.id }) { habitWithStats ->
                        HabitCard(
                            habitWithStats = habitWithStats,
                            onCardClick = { onCardClick(habitWithStats) },
                            onToggleComplete = { onToggleComplete(habitWithStats.habit.id) },
                            modifier = Modifier.testTag("habit_card_${habitWithStats.habit.id}")
                        )
                    }
                }
            }
        }
    }
}
