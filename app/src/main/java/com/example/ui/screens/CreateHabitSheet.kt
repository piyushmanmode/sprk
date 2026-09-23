package com.example.ui.screens

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
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SparkCardBorder
import com.example.ui.theme.SparkFlame
import com.example.ui.theme.SparkOrange
import com.example.ui.theme.SparkPeachLight
import com.example.ui.theme.SparkTextPrimary
import com.example.ui.theme.SparkTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateHabitSheet(
    onDismiss: () -> Unit,
    onCreateHabit: (
        title: String,
        description: String,
        category: String,
        targetDays: Int,
        reminderFreq: String,
        reminderTime: String
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var targetDays by remember { mutableIntStateOf(60) }
    var reminderEnabled by remember { mutableStateOf(true) }
    var reminderFrequency by remember { mutableStateOf("Daily") }
    var reminderTime by remember { mutableStateOf("10:00 AM") }
    var category by remember { mutableStateOf("Growth") }
    var titleTouched by remember { mutableStateOf(false) }
    var showMoreOptions by remember { mutableStateOf(false) }

    val categories = listOf("Growth", "Fitness", "Reading", "Health", "Mindfulness", "Coding")
    val goalOptions = listOf(30, 50, 60, 100)

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = SparkTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Create Habit",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.size(38.dp)) // balance
            }

            Spacer(modifier = Modifier.height(14.dp))

            // White container card
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
                        .imePadding()
                        .navigationBarsPadding()
                        .padding(24.dp)
                ) {
                    // Title Input
                    Text(
                        text = "Habit Title",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SparkTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val isTitleError = titleTouched && title.isBlank()
                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            titleTouched = true
                        },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color(0xFF1E140F),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        placeholder = { Text("e.g. Read 20 pages or Daily Workout", color = Color(0xFFAAA09A)) },
                        shape = RoundedCornerShape(16.dp),
                        isError = isTitleError,
                        supportingText = if (isTitleError) {
                            { Text("Habit title is required", color = Color(0xFFD32F2F), fontSize = 12.sp) }
                        } else null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF1E140F),
                            unfocusedTextColor = Color(0xFF1E140F),
                            focusedBorderColor = SparkOrange,
                            unfocusedBorderColor = SparkCardBorder,
                            focusedContainerColor = SparkPeachLight.copy(alpha = 0.35f),
                            unfocusedContainerColor = Color(0xFFFAF7F5),
                            cursorColor = SparkOrange,
                            focusedPlaceholderColor = Color(0xFFAAA09A),
                            unfocusedPlaceholderColor = Color(0xFFAAA09A),
                            errorBorderColor = Color(0xFFD32F2F)
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("habit_title_input")
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Description Input
                    Text(
                        text = "What do you want to track?",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SparkTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color(0xFF1E140F),
                            fontSize = 14.sp
                        ),
                        placeholder = { Text("e.g. To habituate growth as a designer", color = Color(0xFFAAA09A)) },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF1E140F),
                            unfocusedTextColor = Color(0xFF1E140F),
                            focusedBorderColor = SparkOrange,
                            unfocusedBorderColor = SparkCardBorder,
                            focusedContainerColor = SparkPeachLight.copy(alpha = 0.35f),
                            unfocusedContainerColor = Color(0xFFFAF7F5),
                            cursorColor = SparkOrange,
                            focusedPlaceholderColor = Color(0xFFAAA09A),
                            unfocusedPlaceholderColor = Color(0xFFAAA09A)
                        ),
                        maxLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("habit_description_input")
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Target Days Selector
                    Text(
                        text = "Goal Target (Days)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SparkTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        goalOptions.forEach { days ->
                            val isSelected = targetDays == days
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) SparkOrange else Color(0xFFFAF7F5))
                                    .border(
                                        1.dp,
                                        if (isSelected) SparkOrange else SparkCardBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { targetDays = days }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$days Days",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else SparkTextPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Dates row (Mock dates as shown in mockup)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFFAF7F5),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SparkCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.CalendarToday,
                                        contentDescription = null,
                                        tint = SparkFlame,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Start Date",
                                        fontSize = 14.sp,
                                        color = SparkTextSecondary
                                    )
                                }
                                Text(
                                    text = "Today",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SparkFlame
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .border(1.5.dp, Color(0xFFAAA09A), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "End Date",
                                        fontSize = 14.sp,
                                        color = SparkTextSecondary
                                    )
                                }
                                Text(
                                    text = "In $targetDays Days",
                                    fontSize = 14.sp,
                                    color = Color(0xFFAAA09A)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Reminder section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reminder for you",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SparkTextPrimary
                        )

                        Switch(
                            checked = reminderEnabled,
                            onCheckedChange = { reminderEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SparkOrange
                            )
                        )
                    }

                    if (reminderEnabled) {
                        Spacer(modifier = Modifier.height(10.dp))

                        // Frequency Pills: Daily, Weekly, Monthly
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Daily", "Weekly", "Monthly").forEach { freq ->
                                val isSelected = reminderFrequency == freq
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (isSelected) SparkPeachLight else Color(0xFFFAF7F5))
                                        .border(
                                            1.dp,
                                            if (isSelected) SparkOrange else SparkCardBorder,
                                            RoundedCornerShape(20.dp)
                                        )
                                        .clickable { reminderFrequency = freq }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = freq,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSelected) SparkOrange else SparkTextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Reminder time card
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFFAF7F5),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SparkCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Set your reminder time",
                                        fontSize = 12.sp,
                                        color = SparkTextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = reminderTime,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = SparkTextPrimary
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Filled.AccessTime,
                                    contentDescription = "Time",
                                    tint = SparkOrange,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Primary CTA Button
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onCreateHabit(
                                    title,
                                    description,
                                    category,
                                    targetDays,
                                    if (reminderEnabled) reminderFrequency else "",
                                    reminderTime
                                )
                            } else {
                                titleTouched = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SparkOrange
                        ),
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("start_my_streak_button")
                    ) {
                        Text(
                            text = "Create Habit",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
