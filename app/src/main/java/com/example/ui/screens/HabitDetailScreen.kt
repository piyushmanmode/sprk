package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HabitWithStats
import com.example.ui.components.DotMatrixHeatmap
import com.example.ui.components.SparkFlameIcon
import com.example.ui.theme.SparkCardBorder
import com.example.ui.theme.SparkFlame
import com.example.ui.theme.SparkOrange
import com.example.ui.theme.SparkPeachLight
import com.example.ui.theme.SparkTextPrimary
import com.example.ui.theme.SparkTextSecondary
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun HabitDetailScreen(
    habitWithStats: HabitWithStats,
    onBack: () -> Unit,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    onToggleDay: ((Long) -> Unit)? = null
) {
    val habit = habitWithStats.habit
    val today = LocalDate.now(ZoneId.systemDefault())
    val todayEpochDay = today.toEpochDay()

    // Generate last 7 days (e.g. Sat, Sun, Mon, Tue, Wed, Thu, Fri)
    val past7Days = (6 downTo 0).map { offset ->
        today.minusDays(offset.toLong())
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFF852A),
                        Color(0xFFFF5C1C),
                        Color(0xFFFF4800)
                    )
                )
            )
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Back",
                        tint = SparkTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = habit.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.22f))
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Habit",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // White Card Content
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .navigationBarsPadding()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Big Flame artwork
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(SparkPeachLight),
                        contentAlignment = Alignment.Center
                    ) {
                        SparkFlameIcon(
                            size = 64.dp,
                            showGlow = true
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Current Streak and Record Streak Header
                    Text(
                        text = "${habitWithStats.currentStreak} Days Streak",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SparkTextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    val streakSubtitle = if (habitWithStats.longestStreak > habitWithStats.currentStreak) {
                        "Personal Record: ${habitWithStats.longestStreak} days! Tap any day below to view or log completions."
                    } else if (habitWithStats.currentStreak > 0) {
                        "Your current streak is your all-time personal best! Keep it burning!"
                    } else {
                        "Tap today (or any past day below) to log completions and revive your streak!"
                    }
                    Text(
                        text = streakSubtitle,
                        fontSize = 13.sp,
                        color = SparkTextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Weekly 7-Day Day Pills Strip (Clickable to log/toggle past streaks)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        past7Days.forEach { date ->
                            val dayEpoch = date.toEpochDay()
                            val isDone = habitWithStats.recentCompletions.contains(dayEpoch)
                            val dayName = date.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
                            val isCurrentToday = date == today

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isDone) SparkPeachLight else Color(0xFFF7F3EF))
                                    .border(
                                        1.dp,
                                        if (isCurrentToday) SparkOrange else Color.Transparent,
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable {
                                        if (onToggleDay != null) {
                                            onToggleDay(dayEpoch)
                                        } else if (isCurrentToday) {
                                            onToggleComplete()
                                        }
                                    }
                                    .padding(vertical = 10.dp, horizontal = 8.dp)
                            ) {
                                Text(
                                    text = if (isCurrentToday) "Today" else dayName,
                                    fontSize = 11.sp,
                                    fontWeight = if (isCurrentToday) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isDone) SparkFlame else SparkTextSecondary
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(if (isDone) SparkOrange else Color(0xFFE8DED8)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isDone) {
                                        SparkFlameIcon(size = 14.dp, tint = Color.White)
                                    } else {
                                        Text(
                                            text = "${date.dayOfMonth}",
                                            fontSize = 10.sp,
                                            color = Color(0xFF887A72),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Goal Stats Three-Column Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7F2)),
                            border = BorderStroke(1.dp, SparkCardBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "${habitWithStats.currentStreak}d",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SparkFlame
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Current",
                                    fontSize = 11.sp,
                                    color = SparkTextSecondary
                                )
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7F2)),
                            border = BorderStroke(1.dp, SparkCardBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "${habitWithStats.longestStreak}d",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Best Record",
                                    fontSize = 11.sp,
                                    color = SparkTextSecondary
                                )
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7F2)),
                            border = BorderStroke(1.dp, SparkCardBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "${habitWithStats.totalCompletedDays}d",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SparkOrange
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Total Done",
                                    fontSize = 11.sp,
                                    color = SparkTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Time spent banner
                    val hoursSpent = habit.timeSpentMinutes / 60
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF6F2)),
                        border = BorderStroke(1.dp, SparkCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccessTime,
                                contentDescription = null,
                                tint = SparkFlame,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "${if (hoursSpent > 0) hoursSpent else 12} Hours Well Spent On This Streak",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SparkTextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Dot Matrix Heatmap
                    Text(
                        text = "Consistency Map",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SparkTextPrimary,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    DotMatrixHeatmap(
                        completedDaysCount = habitWithStats.totalCompletedDays,
                        totalSlots = 60,
                        rows = 4,
                        dotSize = 8.dp,
                        dotSpacing = 5.dp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    // Past Days Streak History Log (interactive 14 days)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Past Streak History (Last 14 Days)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SparkTextPrimary
                        )
                        Text(
                            text = "Tap to toggle",
                            fontSize = 11.sp,
                            color = SparkTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val past14Days = (13 downTo 0).map { offset ->
                        today.minusDays(offset.toLong())
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFFCF9F6))
                            .border(1.dp, Color(0xFFF2EBE5), RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        past14Days.chunked(7).forEach { weekChunk ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                weekChunk.forEach { date ->
                                    val dayEpoch = date.toEpochDay()
                                    val isDone = habitWithStats.recentCompletions.contains(dayEpoch)
                                    val isCurrentToday = date == today

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isDone) SparkPeachLight else Color.White)
                                            .border(
                                                1.dp,
                                                if (isCurrentToday) SparkOrange else Color(0xFFEEE5DE),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .clickable {
                                                if (onToggleDay != null) {
                                                    onToggleDay(dayEpoch)
                                                } else if (isCurrentToday) {
                                                    onToggleComplete()
                                                }
                                            }
                                            .padding(horizontal = 6.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = date.dayOfWeek.name.take(1),
                                            fontSize = 9.sp,
                                            fontWeight = if (isCurrentToday) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isDone) SparkFlame else SparkTextSecondary
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(if (isDone) SparkOrange else Color(0xFFEDE5DF)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isDone) {
                                                SparkFlameIcon(size = 11.dp, tint = Color.White)
                                            } else {
                                                Text(
                                                    text = "${date.dayOfMonth}",
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color(0xFF7A6B63)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(26.dp))

                    // Completion Toggle Button
                    Button(
                        onClick = onToggleComplete,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (habitWithStats.isCompletedToday) Color(0xFF10B981) else SparkOrange
                        ),
                        shape = RoundedCornerShape(26.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("toggle_completion_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (habitWithStats.isCompletedToday) "Completed for Today" else "Mark Done for Today",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
