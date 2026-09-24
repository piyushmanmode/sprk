package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CoachInsight
import com.example.data.HabitBreakdownSuggestion
import com.example.model.HabitWithStats
import com.example.ui.CoachMode
import com.example.ui.components.SparkFlameIcon
import com.example.ui.theme.SparkCardBorder
import com.example.ui.theme.SparkFlame
import com.example.ui.theme.SparkOrange
import com.example.ui.theme.SparkPeachLight
import com.example.ui.theme.SparkTextPrimary
import com.example.ui.theme.SparkTextSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CoachBottomSheet(
    coachMode: CoachMode,
    isLoading: Boolean,
    habits: List<HabitWithStats>,
    suggestions: List<HabitBreakdownSuggestion>,
    insight: CoachInsight?,
    onModeChange: (CoachMode) -> Unit,
    onBreakdownGoal: (String) -> Unit,
    onStreakRecovery: (habitTitle: String, previousStreak: Int) -> Unit,
    onWeeklyInsights: () -> Unit,
    onAdoptSuggestion: (HabitBreakdownSuggestion) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var goalInput by remember { mutableStateOf("") }
    var selectedRecoveryHabit by remember { mutableStateOf(habits.firstOrNull()?.habit?.title ?: "") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFFFFFBF8),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = null,
        modifier = modifier
            .statusBarsPadding()
            .testTag("coach_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SparkPeachLight),
                        contentAlignment = Alignment.Center
                    ) {
                        SparkFlameIcon(size = 22.dp, tint = SparkOrange)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AI Habit Coach",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = SparkTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SparkOrange.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Gemini",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SparkOrange,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Behavioral science habit breakdowns & momentum",
                            fontSize = 11.sp,
                            color = SparkTextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF0EBE6))
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = SparkTextPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mode Selector Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF2ECE6))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                CoachTabChip(
                    title = "Goal Breakdown",
                    selected = coachMode == CoachMode.BREAKDOWN,
                    onClick = { onModeChange(CoachMode.BREAKDOWN) },
                    modifier = Modifier.weight(1f)
                )
                CoachTabChip(
                    title = "Recovery",
                    selected = coachMode == CoachMode.RECOVERY,
                    onClick = {
                        onModeChange(CoachMode.RECOVERY)
                        if (selectedRecoveryHabit.isNotBlank()) {
                            val h = habits.find { it.habit.title == selectedRecoveryHabit }
                            onStreakRecovery(selectedRecoveryHabit, h?.longestStreak ?: 3)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
                CoachTabChip(
                    title = "Weekly Insights",
                    selected = coachMode == CoachMode.WEEKLY,
                    onClick = {
                        onModeChange(CoachMode.WEEKLY)
                        onWeeklyInsights()
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Content Area based on mode
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = SparkOrange,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "AI Habit Coach is analyzing...",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = SparkTextSecondary
                        )
                    }
                }
            } else {
                when (coachMode) {
                    CoachMode.BREAKDOWN -> {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "What goal would you like to achieve?",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SparkTextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = goalInput,
                                onValueChange = { goalInput = it },
                                placeholder = { Text("e.g. Get fit, Read 20 books, Meditate", color = Color(0xFFA59891)) },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                textStyle = TextStyle(color = Color(0xFF1E140F), fontSize = 14.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color(0xFF1E140F),
                                    unfocusedTextColor = Color(0xFF1E140F),
                                    focusedBorderColor = SparkOrange,
                                    unfocusedBorderColor = SparkCardBorder,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("coach_goal_input")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Quick prompt pills
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("Get fit", "Read more", "Meditate daily", "Drink water", "Learn Spanish").forEach { pill ->
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = Color.White,
                                        border = BorderStroke(1.dp, Color(0xFFE5DDD6)),
                                        modifier = Modifier.clickable {
                                            goalInput = pill
                                            onBreakdownGoal(pill)
                                        }
                                    ) {
                                        Text(
                                            text = pill,
                                            fontSize = 11.sp,
                                            color = SparkTextPrimary,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { onBreakdownGoal(goalInput) },
                                enabled = goalInput.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = SparkOrange),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("coach_breakdown_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Break Down Into Micro-Habits", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Suggestions List
                            if (suggestions.isNotEmpty()) {
                                Text(
                                    text = "Recommended Tiny Habits (BJ Fogg Formula)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SparkTextPrimary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(suggestions) { item ->
                                        HabitBreakdownCard(
                                            suggestion = item,
                                            onAdopt = { onAdoptSuggestion(item) }
                                        )
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Enter a goal or tap a pill above to generate atomic habit breakdowns.",
                                        fontSize = 12.sp,
                                        color = SparkTextSecondary,
                                        modifier = Modifier.padding(24.dp)
                                    )
                                }
                            }
                        }
                    }

                    CoachMode.RECOVERY -> {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Select habit for streak recovery coaching:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SparkTextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                habits.forEach { h ->
                                    val isSelected = h.habit.title == selectedRecoveryHabit
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = if (isSelected) SparkOrange else Color.White,
                                        border = BorderStroke(1.dp, if (isSelected) SparkOrange else SparkCardBorder),
                                        modifier = Modifier.clickable {
                                            selectedRecoveryHabit = h.habit.title
                                            onStreakRecovery(h.habit.title, h.longestStreak)
                                        }
                                    ) {
                                        Text(
                                            text = h.habit.title,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else SparkTextPrimary,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            if (insight != null) {
                                CoachInsightCard(insight = insight)
                            }
                        }
                    }

                    CoachMode.WEEKLY -> {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Your Weekly Progress & Insights",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SparkTextPrimary
                                )
                                IconButton(
                                    onClick = onWeeklyInsights,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Refresh,
                                        contentDescription = "Refresh",
                                        tint = SparkOrange,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (insight != null) {
                                CoachInsightCard(insight = insight)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CoachTabChip(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (selected) Color.White else Color.Transparent,
        shadowElevation = if (selected) 2.dp else 0.dp,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) SparkOrange else SparkTextSecondary
            )
        }
    }
}

@Composable
private fun HabitBreakdownCard(
    suggestion: HabitBreakdownSuggestion,
    onAdopt: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, SparkCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(SparkPeachLight),
                        contentAlignment = Alignment.Center
                    ) {
                        SparkFlameIcon(size = 14.dp, tint = SparkOrange)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = suggestion.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SparkTextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF5EFE9)
                ) {
                    Text(
                        text = suggestion.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SparkTextSecondary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Trigger anchor cue
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Anchor: ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SparkOrange
                )
                Text(
                    text = suggestion.triggerCue,
                    fontSize = 11.sp,
                    color = SparkTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Action
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Action: ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
                Text(
                    text = suggestion.action,
                    fontSize = 11.sp,
                    color = SparkTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onAdopt,
                colors = ButtonDefaults.buttonColors(containerColor = SparkOrange),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add to My Streaks", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CoachInsightCard(insight: CoachInsight) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, SparkCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Lightbulb,
                    contentDescription = null,
                    tint = SparkOrange,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = insight.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SparkTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = insight.message,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = SparkTextPrimary
            )

            if (insight.tips.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Actionable Momentum Principles:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SparkOrange
                )
                Spacer(modifier = Modifier.height(6.dp))
                insight.tips.forEach { tip ->
                    Row(
                        modifier = Modifier.padding(vertical = 3.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "• ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SparkOrange
                        )
                        Text(
                            text = tip,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = SparkTextSecondary
                        )
                    }
                }
            }
        }
    }
}
