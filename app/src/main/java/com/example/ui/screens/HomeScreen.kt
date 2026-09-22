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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.ui.theme.SparkFlame
import com.example.ui.theme.SparkOrange
import com.example.ui.theme.SparkPeachLight
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    userName: String,
    habits: List<HabitWithStats>,
    onAvatarClick: () -> Unit,
    onCardClick: (HabitWithStats) -> Unit,
    onToggleComplete: (Long) -> Unit,
    onAddHabitClick: () -> Unit,
    onWidgetOptionsClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedEpochDay: Long = LocalDate.now(ZoneId.systemDefault()).toEpochDay(),
    onSelectDate: (Long) -> Unit = {},
    onToggleDay: ((Long, Long) -> Unit)? = null
) {
    val greetingTime = when (LocalTime.now().hour) {
        in 5..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }

    val today = LocalDate.now(ZoneId.systemDefault())
    val todayEpoch = today.toEpochDay()
    val isViewingToday = selectedEpochDay == todayEpoch
    val selectedDate = LocalDate.ofEpochDay(selectedEpochDay)

    // Last 7 days for the interactive date selector bar
    val past7Days = remember(todayEpoch) {
        (6 downTo 0).map { today.minusDays(it.toLong()) }
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
                .height(300.dp)
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
                    .padding(horizontal = 20.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "$greetingTime ${userName.split(" ").firstOrNull() ?: userName}",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Keep the streak alive,\nspark your daily motivation.",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 26.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Interactive Day Carousel / Date Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                past7Days.forEach { date ->
                    val dayEpoch = date.toEpochDay()
                    val isSelected = dayEpoch == selectedEpochDay
                    val isDayToday = dayEpoch == todayEpoch
                    val completedCountOnDay = habits.count { it.recentCompletions.contains(dayEpoch) }
                    val dayLabel = if (isDayToday) "Today" else date.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                when {
                                    isSelected -> Color.White
                                    isDayToday -> Color.White.copy(alpha = 0.28f)
                                    else -> Color.White.copy(alpha = 0.16f)
                                }
                            )
                            .clickable { onSelectDate(dayEpoch) }
                            .padding(horizontal = 8.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = dayLabel,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected || isDayToday) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) SparkFlame else Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isSelected && completedCountOnDay > 0 -> SparkOrange
                                        isSelected -> Color(0xFFF7EBE4)
                                        completedCountOnDay > 0 -> Color.White
                                        else -> Color.White.copy(alpha = 0.25f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (completedCountOnDay > 0) {
                                SparkFlameIcon(
                                    size = 13.dp,
                                    tint = if (isSelected) Color.White else SparkFlame
                                )
                            } else {
                                Text(
                                    text = "${date.dayOfMonth}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) SparkFlame else Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Past Day Banner indicator (if viewing a past day)
            if (!isViewingToday) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    shadowElevation = 3.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SparkFlameIcon(size = 16.dp, tint = SparkFlame)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Viewing: ${selectedDate.format(DateTimeFormatter.ofPattern("EEE, MMM d"))}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E130D)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(SparkPeachLight)
                                .clickable { onSelectDate(todayEpoch) }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Back to Today",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SparkFlame
                            )
                        }
                    }
                }
            }

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
                            text = "Create your first habit to ignite your daily spark!",
                            fontSize = 14.sp,
                            color = Color(0xFF827067)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onAddHabitClick,
                            colors = ButtonDefaults.buttonColors(containerColor = SparkOrange),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.testTag("create_first_habit_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Create Your First Habit", fontWeight = FontWeight.SemiBold)
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
                            selectedEpochDay = selectedEpochDay,
                            onToggleDay = { dayEpoch ->
                                if (onToggleDay != null) {
                                    onToggleDay(habitWithStats.habit.id, dayEpoch)
                                } else {
                                    onSelectDate(dayEpoch)
                                }
                            },
                            modifier = Modifier.testTag("habit_card_${habitWithStats.habit.id}")
                        )
                    }
                }
            }
        }
    }
}
