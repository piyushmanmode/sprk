package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.model.Trophy
import com.example.ui.components.SparkFlameIcon
import com.example.ui.theme.SparkCardBorder
import com.example.ui.theme.SparkFlame
import com.example.ui.theme.SparkOrange
import com.example.ui.theme.SparkPeachLight
import com.example.ui.theme.SparkTextPrimary
import com.example.ui.theme.SparkTextSecondary
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

@Composable
fun StreaksOverviewScreen(
    habits: List<HabitWithStats>,
    trophies: List<Trophy>,
    onTrophyClick: (Trophy) -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedMonthOffset by remember { mutableStateOf(0) }
    val baseMonth = YearMonth.now().plusMonths(selectedMonthOffset.toLong())
    val monthName = baseMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }
    val daysInMonth = baseMonth.lengthOfMonth()

    // Aggregate all completion epoch days across all habits
    val allCompletedEpochDays = habits.flatMap { it.recentCompletions }.toSet()

    // Key Stats
    val activeStreak = habits.maxOfOrNull { it.currentStreak } ?: 0
    val longestStreak = habits.maxOfOrNull { it.longestStreak } ?: 0
    val activeDaysThisMonth = (1..daysInMonth).count { day ->
        val date = baseMonth.atDay(day)
        allCompletedEpochDays.contains(date.toEpochDay())
    }
    val noActivityDays = maxOf(0, daysInMonth - activeDaysThisMonth)

    val unlockedTrophies = trophies.filter { it.isUnlocked }

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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onBack != null) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.22f))
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ChevronLeft,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    Column {
                        Text(
                            text = "Streaks",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Overview",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Month switcher arrows
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.22f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    IconButton(
                        onClick = { selectedMonthOffset-- },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ChevronLeft,
                            contentDescription = "Previous Month",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "$monthName ${baseMonth.year}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    IconButton(
                        onClick = { selectedMonthOffset++ },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = "Next Month",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // White container
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color.White
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Calendar Section
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Days of week header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                listOf("Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri").forEach { day ->
                                    Text(
                                        text = day,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFA59891),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Calculate start offset
                            val firstDayOfMonth = baseMonth.atDay(1)
                            val dayOfWeekVal = firstDayOfMonth.dayOfWeek.value // 1 (Mon) to 7 (Sun)
                            // Sat=0, Sun=1, Mon=2, Tue=3, Wed=4, Thu=5, Fri=6
                            val leadingEmptySlots = (dayOfWeekVal + 1) % 7

                            val totalCells = leadingEmptySlots + daysInMonth
                            val totalRows = (totalCells + 6) / 7

                            for (rowIndex in 0 until totalRows) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    for (colIndex in 0..6) {
                                        val slotIndex = rowIndex * 7 + colIndex
                                        val dayNumber = slotIndex - leadingEmptySlots + 1

                                        if (dayNumber in 1..daysInMonth) {
                                            val date = baseMonth.atDay(dayNumber)
                                            val isCompleted = allCompletedEpochDays.contains(date.toEpochDay())

                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(38.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (isCompleted) {
                                                    // Completed day: Orange flame badge
                                                    Box(
                                                        modifier = Modifier
                                                            .size(34.dp)
                                                            .clip(CircleShape)
                                                            .background(SparkOrange),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        SparkFlameIcon(size = 18.dp, tint = Color.White)
                                                    }
                                                } else {
                                                    Text(
                                                        text = String.format("%02d", dayNumber),
                                                        fontSize = 13.sp,
                                                        color = Color(0xFF6B5C55),
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(38.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Three Summary Metric Cards Row
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MetricCard(
                                title = "Active Streak",
                                value = String.format("%02d", activeStreak),
                                modifier = Modifier.weight(1f)
                            )
                            MetricCard(
                                title = "Longest Streak",
                                value = "${String.format("%02d", longestStreak)} Days",
                                modifier = Modifier.weight(1.1f)
                            )
                            MetricCard(
                                title = "No Activity",
                                value = "${String.format("%02d", noActivityDays)} Days",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Trophies Achieved Section
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.EmojiEvents,
                                        contentDescription = null,
                                        tint = SparkFlame,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Trophy Achieved",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SparkTextPrimary
                                    )
                                }

                                Text(
                                    text = "${unlockedTrophies.size} Earned",
                                    fontSize = 13.sp,
                                    color = SparkFlame,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (unlockedTrophies.isEmpty()) {
                                Text(
                                    text = "Keep streaks active to unlock your first trophy!",
                                    fontSize = 13.sp,
                                    color = SparkTextSecondary
                                )
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    unlockedTrophies.forEach { trophy ->
                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = Color(0xFFFFFBF8),
                                            border = BorderStroke(1.dp, SparkCardBorder),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { onTrophyClick(trophy) }
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(14.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    SparkFlameIcon(size = 18.dp, tint = SparkFlame)
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Column {
                                                        Text(
                                                            text = trophy.title,
                                                            fontSize = 14.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = SparkTextPrimary
                                                        )
                                                        Text(
                                                            text = trophy.habitTitle ?: trophy.description,
                                                            fontSize = 12.sp,
                                                            color = SparkTextSecondary,
                                                            maxLines = 1
                                                        )
                                                    }
                                                }

                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .background(SparkOrange)
                                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                                ) {
                                                    Text(
                                                        text = "Unlocked",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7F2)),
        border = BorderStroke(1.dp, SparkCardBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                color = SparkTextSecondary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SparkTextPrimary
            )
        }
    }
}
