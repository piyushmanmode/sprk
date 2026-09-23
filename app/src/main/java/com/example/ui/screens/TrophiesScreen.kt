package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.HabitWithStats
import com.example.model.Trophy
import com.example.ui.components.SparkFlameIcon
import com.example.ui.theme.SparkCardBorder
import com.example.ui.theme.SparkFlame
import com.example.ui.theme.SparkOrange
import com.example.ui.theme.SparkPeachLight
import com.example.ui.theme.SparkTextPrimary
import com.example.ui.theme.SparkTextSecondary

@Composable
fun TrophiesScreen(
    trophies: List<Trophy>,
    habits: List<HabitWithStats> = emptyList(),
    onTrophyClick: (Trophy) -> Unit,
    onBack: () -> Unit = {},
    onShowStreaksOverview: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val unlockedCount = trophies.count { it.isUnlocked }

    // Best streaks across all habits
    val bestActiveStreak = habits.maxOfOrNull { it.currentStreak } ?: 0
    val longestAllTimeStreak = habits.maxOfOrNull { it.longestStreak } ?: 0
    val totalCompletions = habits.sumOf { it.totalCompletedDays }

    // Group trophies by habit title or category
    val grouped = trophies.groupBy { it.habitTitle ?: "Global Streak Milestones" }

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
            // Top App Bar with Backward Arrow
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.22f))
                            .testTag("trophies_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Trophies",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Flame + Unlocked Count badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.25f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    SparkFlameIcon(size = 16.dp, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${unlockedCount}/${trophies.size}",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // White body container
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color(0xFFFFFBF8)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Streaks Overview & Flame Connection Banner
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            border = BorderStroke(1.dp, SparkCardBorder),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onShowStreaksOverview?.invoke() }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(SparkPeachLight),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            SparkFlameIcon(size = 22.dp, tint = SparkOrange)
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "Streak Milestones Hub",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SparkTextPrimary
                                            )
                                            Text(
                                                text = "Trophies are unlocked by continuous habit streaks",
                                                fontSize = 12.sp,
                                                color = SparkTextSecondary
                                            )
                                        }
                                    }

                                    if (onShowStreaksOverview != null) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                            contentDescription = "View Streaks",
                                            tint = SparkOrange
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Quick streak stats row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    StreakStatChip(
                                        title = "Active Flame",
                                        value = "$bestActiveStreak Days",
                                        modifier = Modifier.weight(1f)
                                    )
                                    StreakStatChip(
                                        title = "Longest Streak",
                                        value = "$longestAllTimeStreak Days",
                                        modifier = Modifier.weight(1f)
                                    )
                                    StreakStatChip(
                                        title = "Total Check-ins",
                                        value = "$totalCompletions",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // Trophies grouped by habit/category
                    grouped.forEach { (categoryTitle, trophyList) ->
                        item(key = categoryTitle) {
                            Column {
                                // Section header row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = categoryTitle,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SparkTextPrimary
                                    )

                                    val sectionUnlocked = trophyList.count { it.isUnlocked }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(SparkPeachLight)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        SparkFlameIcon(size = 12.dp, tint = SparkFlame)
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "${sectionUnlocked}/${trophyList.size}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SparkFlame
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Grid of Trophy Cards for this section
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    trophyList.take(2).forEach { trophy ->
                                        val matchingStreak = getMatchingStreak(trophy, habits)
                                        TrophyCardWithStreak(
                                            trophy = trophy,
                                            currentStreak = matchingStreak,
                                            onClick = {
                                                if (trophy.isUnlocked) {
                                                    onTrophyClick(trophy)
                                                } else {
                                                    val needed = (trophy.requiredDays - matchingStreak).coerceAtLeast(1)
                                                    Toast.makeText(
                                                        context,
                                                        "Locked: $needed more consecutive streak day${if (needed > 1) "s" else ""} needed!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }

                                if (trophyList.size > 2) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        trophyList.drop(2).take(2).forEach { trophy ->
                                            val matchingStreak = getMatchingStreak(trophy, habits)
                                            TrophyCardWithStreak(
                                                trophy = trophy,
                                                currentStreak = matchingStreak,
                                                onClick = {
                                                    if (trophy.isUnlocked) {
                                                        onTrophyClick(trophy)
                                                    } else {
                                                        val needed = (trophy.requiredDays - matchingStreak).coerceAtLeast(1)
                                                        Toast.makeText(
                                                            context,
                                                            "Locked: $needed more consecutive streak day${if (needed > 1) "s" else ""} needed!",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                },
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                        if (trophyList.drop(2).size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
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

private fun getMatchingStreak(trophy: Trophy, habits: List<HabitWithStats>): Int {
    val matchingHabit = habits.find { it.habit.title.equals(trophy.habitTitle, ignoreCase = true) }
    return if (matchingHabit != null) {
        maxOf(matchingHabit.currentStreak, matchingHabit.longestStreak)
    } else {
        // Global trophy: best streak across any habit
        habits.maxOfOrNull { maxOf(it.currentStreak, it.longestStreak) } ?: 0
    }
}

@Composable
private fun StreakStatChip(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFAF7F5),
        border = BorderStroke(1.dp, Color(0xFFEFE8E2)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = SparkOrange
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = SparkTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TrophyCardWithStreak(
    trophy: Trophy,
    currentStreak: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = (currentStreak.toFloat() / trophy.requiredDays.toFloat()).coerceIn(0f, 1f)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (trophy.isUnlocked) Color.White else Color(0xFFF7F2EE)
        ),
        border = BorderStroke(1.dp, if (trophy.isUnlocked) SparkCardBorder else Color(0xFFE8DED8)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (trophy.isUnlocked) 2.dp else 0.dp),
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("trophy_card_${trophy.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon / Trophy Visual
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(if (trophy.isUnlocked) SparkPeachLight else Color(0xFFEDE5DF)),
                contentAlignment = Alignment.Center
            ) {
                if (trophy.isUnlocked) {
                    Image(
                        painter = painterResource(id = R.drawable.trophy_gold_badge),
                        contentDescription = trophy.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(50.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Locked",
                        tint = Color(0xFFA59891),
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Status Badge (Unlocked / Streak target)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (trophy.isUnlocked) SparkOrange else Color(0xFFA59891))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (trophy.isUnlocked) "Unlocked 🔥" else "${trophy.requiredDays}D Streak",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Title
            Text(
                text = trophy.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (trophy.isUnlocked) SparkTextPrimary else Color(0xFFA59891),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Streak Progress Bar linking streaks directly to trophy
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = { if (trophy.isUnlocked) 1f else progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (trophy.isUnlocked) SparkOrange else Color(0xFFFF9E3D),
                    trackColor = Color(0xFFE8DFD8),
                    strokeCap = StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (trophy.isUnlocked) {
                        "Streak Mastered"
                    } else {
                        "Streak: $currentStreak/${trophy.requiredDays}d"
                    },
                    fontSize = 10.sp,
                    color = if (trophy.isUnlocked) SparkOrange else SparkTextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
