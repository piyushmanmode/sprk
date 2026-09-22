package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HabitWithStats
import com.example.ui.theme.SparkCardBorder
import com.example.ui.theme.SparkFlame
import com.example.ui.theme.SparkOrange
import com.example.ui.theme.SparkPeachLight
import com.example.ui.theme.SparkTextPrimary
import com.example.ui.theme.SparkTextSecondary
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun HabitCard(
    habitWithStats: HabitWithStats,
    onCardClick: () -> Unit,
    onToggleComplete: () -> Unit,
    modifier: Modifier = Modifier,
    selectedEpochDay: Long = LocalDate.now(ZoneId.systemDefault()).toEpochDay(),
    onToggleDay: ((Long) -> Unit)? = null
) {
    val habit = habitWithStats.habit
    val isDoneForSelectedDay = habitWithStats.recentCompletions.contains(selectedEpochDay)
    val today = LocalDate.now(ZoneId.systemDefault())
    val todayEpochDay = today.toEpochDay()
    val isViewingToday = selectedEpochDay == todayEpochDay

    val checkboxBg by animateColorAsState(
        targetValue = if (isDoneForSelectedDay) SparkOrange else Color.Transparent,
        label = "checkboxBg"
    )

    // Last 7 days for visual past streak history
    val past7Days = remember(todayEpochDay) {
        (6 downTo 0).map { today.minusDays(it.toLong()) }
    }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, SparkCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header row: Streak pill badge, best milestone pill, and Checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Active Streak badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(SparkPeachLight)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        SparkFlameIcon(size = 14.dp, tint = SparkFlame)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "${habitWithStats.currentStreak} Days Streak",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SparkFlame
                        )
                    }

                    // Best / Past Streak badge (shows if best streak > 0)
                    if (habitWithStats.longestStreak > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFFFF3E0))
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.EmojiEvents,
                                contentDescription = null,
                                tint = Color(0xFFF57C00),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Best: ${habitWithStats.longestStreak}d",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100)
                            )
                        }
                    }
                }

                // Checkbox for selected day
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(checkboxBg)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onToggleComplete
                        )
                        .then(
                            if (!isDoneForSelectedDay) {
                                Modifier.background(
                                    color = Color(0xFFF6EDE8),
                                    shape = RoundedCornerShape(9.dp)
                                )
                            } else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDoneForSelectedDay) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = if (isViewingToday) "Done for today" else "Done for selected day",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Habit title
            Text(
                text = habit.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SparkTextPrimary,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 7-Day Past Streak Timeline Mini-Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFDFBF9))
                    .border(1.dp, Color(0xFFF2EAE5), RoundedCornerShape(14.dp))
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                past7Days.forEach { date ->
                    val dayEpoch = date.toEpochDay()
                    val isDayDone = habitWithStats.recentCompletions.contains(dayEpoch)
                    val isThisDaySelected = dayEpoch == selectedEpochDay
                    val dayLetter = date.dayOfWeek.name.take(1)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isThisDaySelected) SparkPeachLight else Color.Transparent)
                            .clickable {
                                if (onToggleDay != null) {
                                    onToggleDay(dayEpoch)
                                } else {
                                    onCardClick()
                                }
                            }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = dayLetter,
                            fontSize = 10.sp,
                            fontWeight = if (isThisDaySelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isDayDone) SparkFlame else SparkTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isDayDone -> SparkOrange
                                        isThisDaySelected -> Color(0xFFF4DFD5)
                                        else -> Color(0xFFECE4DF)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDayDone) {
                                SparkFlameIcon(size = 11.dp, tint = Color.White)
                            } else {
                                Text(
                                    text = "${date.dayOfMonth}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF8D7F77)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress text & Heatmap
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "${habitWithStats.totalCompletedDays}/${habit.targetDays} days completed",
                    fontSize = 13.sp,
                    color = SparkTextSecondary,
                    fontWeight = FontWeight.Normal
                )

                DotMatrixHeatmap(
                    completedDaysCount = habitWithStats.totalCompletedDays,
                    totalSlots = 36,
                    rows = 3,
                    dotSize = 5.5.dp,
                    dotSpacing = 3.5.dp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }
}
