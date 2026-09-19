package com.example.ui.screens

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DashboardCustomize
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
import com.example.widget.HeatmapWidgetProvider
import com.example.widget.SparkFlameWidgetProvider
import com.example.widget.StreaksWidgetProvider
import com.example.widget.TasksWidgetProvider

enum class WidgetType(
    val title: String,
    val subtitle: String,
    val defaultSize: String,
    val icon: ImageVector,
    val providerClass: Class<*>
) {
    STREAKS(
        title = "1. Streaks Widget",
        subtitle = "Real-time active streak status & flame counter",
        defaultSize = "2 × 2 / 4 × 2",
        icon = Icons.Filled.LocalFireDepartment,
        providerClass = StreaksWidgetProvider::class.java
    ),
    TASKS(
        title = "2. Tasks & Habits Widget",
        subtitle = "Check off daily habits directly from your home screen",
        defaultSize = "4 × 2 / 4 × 3",
        icon = Icons.Filled.FormatListBulleted,
        providerClass = TasksWidgetProvider::class.java
    ),
    FLAME_MOTIVATION(
        title = "3. Spark Flame & Motivation",
        subtitle = "Glowing streak level badge and daily inspirational spark",
        defaultSize = "2 × 2 / 4 × 1",
        icon = Icons.Filled.Widgets,
        providerClass = SparkFlameWidgetProvider::class.java
    ),
    HEATMAP(
        title = "4. Consistency Heatmap",
        subtitle = "30-day dot matrix consistency map at a glance",
        defaultSize = "4 × 2 Expanded",
        icon = Icons.Filled.GridOn,
        providerClass = HeatmapWidgetProvider::class.java
    )
}

enum class WidgetThemeOption(val label: String) {
    WARM_FLAME("Warm Flame"),
    CLEAN_WHITE("Clean White"),
    MIDNIGHT_DARK("Midnight Dark")
}

@Composable
fun WidgetOptionsScreen(
    habits: List<HabitWithStats>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedWidgetType by remember { mutableStateOf(WidgetType.STREAKS) }
    var selectedTheme by remember { mutableStateOf(WidgetThemeOption.WARM_FLAME) }

    fun pinWidgetToHomeScreen(widgetType: WidgetType) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                val provider = ComponentName(context, widgetType.providerClass)
                val successIntent = Intent().apply {
                    putExtra("PINNED_WIDGET", widgetType.name)
                }
                val successPendingIntent = PendingIntent.getBroadcast(
                    context,
                    widgetType.ordinal,
                    successIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val pinned = appWidgetManager.requestPinAppWidget(provider, null, successPendingIntent)
                if (pinned) {
                    Toast.makeText(context, "Adding ${widgetType.title} to Home Screen...", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Pin prompt open on launcher", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(
                    context,
                    "Manual add: Long-press Home Screen -> Widgets -> Spark",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(
                context,
                "Long-press Home Screen -> Widgets -> Spark to add",
                Toast.LENGTH_LONG
            ).show()
        }
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

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Widget Options",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Customize your home screen",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
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
                    // Widget Type Selector Row
                    item {
                        Column {
                            Text(
                                text = "Select Widget Style",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = SparkTextPrimary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                WidgetType.values().forEach { type ->
                                    val isSelected = selectedWidgetType == type
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(if (isSelected) SparkOrange else Color(0xFFFAF6F2))
                                            .border(
                                                1.dp,
                                                if (isSelected) SparkOrange else SparkCardBorder,
                                                RoundedCornerShape(16.dp)
                                            )
                                            .clickable { selectedWidgetType = type }
                                            .padding(vertical = 12.dp, horizontal = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = type.icon,
                                                contentDescription = type.title,
                                                tint = if (isSelected) Color.White else SparkFlame,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = when (type) {
                                                    WidgetType.STREAKS -> "Streaks"
                                                    WidgetType.TASKS -> "Tasks"
                                                    WidgetType.FLAME_MOTIVATION -> "Flame"
                                                    WidgetType.HEATMAP -> "Heatmap"
                                                },
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else SparkTextPrimary,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Live Interactive Preview Section
                    item {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Live Launcher Preview",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SparkTextPrimary
                                )

                                Text(
                                    text = selectedWidgetType.defaultSize,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SparkFlame
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Phone Wallpaper Simulation Container
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                Color(0xFF2C221D),
                                                Color(0xFF140F0D)
                                            )
                                        )
                                    )
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                LiveWidgetPreviewCard(
                                    type = selectedWidgetType,
                                    theme = selectedTheme,
                                    habits = habits
                                )
                            }
                        }
                    }

                    // Theme Selector Chips
                    item {
                        Column {
                            Text(
                                text = "Preview Theme",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SparkTextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                WidgetThemeOption.values().forEach { themeOption ->
                                    val isSelected = selectedTheme == themeOption
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) SparkPeachLight else Color(0xFFFAF7F5))
                                            .border(
                                                1.dp,
                                                if (isSelected) SparkOrange else SparkCardBorder,
                                                RoundedCornerShape(12.dp)
                                            )
                                            .clickable { selectedTheme = themeOption }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = themeOption.label,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isSelected) SparkOrange else SparkTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Primary Action: Add to Home Screen Button
                    item {
                        Button(
                            onClick = { pinWidgetToHomeScreen(selectedWidgetType) },
                            colors = ButtonDefaults.buttonColors(containerColor = SparkOrange),
                            shape = RoundedCornerShape(26.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("add_widget_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Add to Home Screen",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // How to add manually guide
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBF8)),
                            border = BorderStroke(1.dp, SparkCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "How to add from Launcher",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SparkTextPrimary
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                StepGuideItem(
                                    number = "1",
                                    text = "Touch and hold an empty spot on your Android home screen."
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                StepGuideItem(
                                    number = "2",
                                    text = "Tap Widgets and scroll to find Spark."
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                StepGuideItem(
                                    number = "3",
                                    text = "Choose Streaks, Tasks, Flame, or Heatmap and place it."
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepGuideItem(
    number: String,
    text: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(SparkOrange),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = SparkTextSecondary
        )
    }
}

@Composable
private fun LiveWidgetPreviewCard(
    type: WidgetType,
    theme: WidgetThemeOption,
    habits: List<HabitWithStats>,
    modifier: Modifier = Modifier
) {
    val topHabit = habits.maxByOrNull { it.currentStreak }
    val maxStreak = topHabit?.currentStreak ?: 12
    val habitTitle = topHabit?.habit?.title ?: "Read 20 pages habit book"

    val (cardBgBrush, textColor, subtextColor, badgeBg, badgeTextColor) = when (theme) {
        WidgetThemeOption.WARM_FLAME -> Tuple5(
            Brush.linearGradient(listOf(Color(0xFFFF852A), Color(0xFFFF5C1C), Color(0xFFFF4500))),
            Color.White,
            Color.White.copy(alpha = 0.85f),
            Color.White.copy(alpha = 0.25f),
            Color.White
        )
        WidgetThemeOption.CLEAN_WHITE -> Tuple5(
            Brush.linearGradient(listOf(Color.White, Color.White)),
            Color(0xFF1E130D),
            Color(0xFF827067),
            SparkPeachLight,
            SparkOrange
        )
        WidgetThemeOption.MIDNIGHT_DARK -> Tuple5(
            Brush.linearGradient(listOf(Color(0xFF221A16), Color(0xFF1A130F))),
            Color.White,
            Color(0xFFB0A29A),
            Color(0xFF382920),
            SparkOrange
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(cardBgBrush)
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(22.dp))
            .padding(16.dp)
    ) {
        when (type) {
            WidgetType.STREAKS -> {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SparkFlameIcon(size = 18.dp, tint = if (theme == WidgetThemeOption.WARM_FLAME) Color.White else SparkOrange)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SPARK STREAKS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(badgeBg)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "🔥 ON FIRE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeTextColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "$maxStreak Days",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textColor
                    )
                    Text(
                        text = habitTitle,
                        fontSize = 12.sp,
                        color = subtextColor,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tap to log today's streak",
                            fontSize = 11.sp,
                            color = subtextColor
                        )
                        Text(
                            text = "Open Spark →",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (theme == WidgetThemeOption.WARM_FLAME) Color.White else SparkOrange
                        )
                    }
                }
            }
            WidgetType.TASKS -> {
                val doneCount = habits.count { it.isCompletedToday }
                val totalCount = habits.size
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = if (theme == WidgetThemeOption.WARM_FLAME) Color.White else SparkOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "TODAY'S HABITS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(badgeBg)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "$doneCount/$totalCount Done",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeTextColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    habits.take(3).forEach { habitWithStats ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = if (habitWithStats.isCompletedToday) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (habitWithStats.isCompletedToday) (if (theme == WidgetThemeOption.WARM_FLAME) Color.White else SparkOrange) else subtextColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = habitWithStats.habit.title,
                                fontSize = 12.sp,
                                color = textColor,
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Tap to check off habits →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (theme == WidgetThemeOption.WARM_FLAME) Color.White else SparkOrange,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
            WidgetType.FLAME_MOTIVATION -> {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SparkFlameIcon(size = 20.dp, tint = if (theme == WidgetThemeOption.WARM_FLAME) Color.White else SparkOrange)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SPARK MOTIVATION",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(badgeBg)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "🔥 $maxStreak STREAK",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeTextColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "“Keep the streak alive, spark your daily motivation.”",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "All daily goals tracked & ready",
                        fontSize = 11.sp,
                        color = subtextColor
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Spark Habit Tracker",
                            fontSize = 10.sp,
                            color = subtextColor
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (theme == WidgetThemeOption.WARM_FLAME) Color.White else SparkOrange)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "+ Log Streak",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (theme == WidgetThemeOption.WARM_FLAME) SparkOrange else Color.White
                            )
                        }
                    }
                }
            }
            WidgetType.HEATMAP -> {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SparkFlameIcon(size = 18.dp, tint = if (theme == WidgetThemeOption.WARM_FLAME) Color.White else SparkOrange)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CONSISTENCY MAP",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(badgeBg)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "$maxStreak Days Streak",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeTextColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    DotMatrixHeatmap(
                        completedDaysCount = maxStreak * 2,
                        totalSlots = 32,
                        rows = 4,
                        dotSize = 7.dp,
                        dotSpacing = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "View Full Month Overview →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (theme == WidgetThemeOption.WARM_FLAME) Color.White else SparkOrange,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}

private data class Tuple5<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)
