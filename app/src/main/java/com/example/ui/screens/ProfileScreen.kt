package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.HabitWithStats
import com.example.model.Trophy
import com.example.ui.components.NavTab
import com.example.ui.theme.SparkCardBorder
import com.example.ui.theme.SparkFlame
import com.example.ui.theme.SparkOrange
import com.example.ui.theme.SparkPeachLight
import com.example.ui.theme.SparkTextPrimary
import com.example.ui.theme.SparkTextSecondary

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import java.io.File

@Composable
fun ProfileScreen(
    userName: String,
    userEmail: String,
    notificationsEnabled: Boolean,
    habits: List<HabitWithStats>,
    trophies: List<Trophy>,
    onNavigateTab: (NavTab) -> Unit,
    onShowStreaksOverview: () -> Unit,
    onShowWidgetOptions: () -> Unit,
    onUpdateProfile: (name: String, email: String, notifications: Boolean) -> Unit,
    onResetDemoData: () -> Unit,
    onShowWelcome: () -> Unit,
    modifier: Modifier = Modifier,
    profilePhotoPath: String? = null,
    onSelectProfilePhoto: (Uri) -> Unit = {},
    onRemoveProfilePhoto: () -> Unit = {},
    onOpenCoach: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onSelectProfilePhoto(uri)
        }
    }

    var showEditDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var editName by remember(userName) { mutableStateOf(userName) }
    var editEmail by remember(userEmail) { mutableStateOf(userEmail) }

    val activeStreakCount = habits.count { it.currentStreak > 0 }
    val unlockedTrophyCount = trophies.count { it.isUnlocked }

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
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    Text(
                        text = "Profile",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                IconButton(
                    onClick = { showEditDialog = true },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.22f))
                ) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Edit Profile",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

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
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Avatar & User Details
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .border(3.dp, SparkOrange, CircleShape)
                                    .clickable {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                    .testTag("profile_avatar_picker"),
                                contentAlignment = Alignment.Center
                            ) {
                                val hasCustomPhoto = !profilePhotoPath.isNullOrBlank() && File(profilePhotoPath).exists()
                                if (hasCustomPhoto) {
                                    AsyncImage(
                                        model = File(profilePhotoPath!!),
                                        contentDescription = "User Avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.avatar_user),
                                        contentDescription = "User Avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                    )
                                }

                                // Zero-permission Photo Picker badge
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(SparkOrange)
                                        .border(2.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.PhotoCamera,
                                        contentDescription = "Pick Profile Photo",
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Tap photo to change (Zero-Permission)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = SparkOrange,
                                    modifier = Modifier
                                        .clickable {
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        }
                                        .testTag("change_photo_text")
                                )
                                if (!profilePhotoPath.isNullOrBlank() && File(profilePhotoPath).exists()) {
                                    Text(
                                        text = " • ",
                                        fontSize = 11.sp,
                                        color = SparkTextSecondary
                                    )
                                    Text(
                                        text = "Remove",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Red.copy(alpha = 0.8f),
                                        modifier = Modifier.clickable { onRemoveProfilePhoto() }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (userName.isNotBlank()) userName else "Tap to set your name",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = SparkTextPrimary,
                                modifier = Modifier.clickable { showEditDialog = true }
                            )

                            if (userEmail.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = userEmail,
                                    fontSize = 13.sp,
                                    color = SparkTextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "$activeStreakCount Active Streak • $unlockedTrophyCount Trophy Achieved",
                                fontSize = 13.sp,
                                color = SparkOrange,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(10.dp)) }

                    if (onOpenCoach != null) {
                        item {
                            ProfileMenuCard(
                                icon = Icons.Filled.AutoAwesome,
                                title = "AI Habit Coach (Gemini)",
                                subtitle = "Goal Breakdowns & Streak Recovery",
                                onClick = onOpenCoach
                            )
                        }
                    }

                    // Menu Item: Account Details
                    item {
                        ProfileMenuCard(
                            icon = Icons.Filled.AccountCircle,
                            title = "Account Details",
                            subtitle = "Update Your Personal Info",
                            onClick = { showEditDialog = true }
                        )
                    }

                    // Menu Item: Reminder Preferences
                    item {
                        ProfileMenuCard(
                            icon = Icons.Filled.Schedule,
                            title = "Reminder Preferences",
                            subtitle = "Daily / Weekly / Custom Time",
                            onClick = { showEditDialog = true }
                        )
                    }

                    // Menu Item: Push Notification (with Switch toggle)
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBF8)),
                            border = BorderStroke(1.dp, SparkCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Daily Reminders",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SparkTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Get notified for daily habit check-ins",
                                        fontSize = 12.sp,
                                        color = SparkTextSecondary
                                    )
                                }

                                Switch(
                                    checked = notificationsEnabled,
                                    onCheckedChange = { isEnabled ->
                                        onUpdateProfile(userName, userEmail, isEnabled)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = SparkOrange
                                    )
                                )
                            }
                        }
                    }

                    // Menu Item: Trophy & Achievements
                    item {
                        ProfileMenuCard(
                            icon = Icons.Filled.EmojiEvents,
                            title = "Trophy & Achievements",
                            subtitle = "View & Share Your Achievement",
                            onClick = { onNavigateTab(NavTab.TROPHIES) }
                        )
                    }

                    // Menu Item: Streaks Overview
                    item {
                        ProfileMenuCard(
                            icon = Icons.Filled.CalendarMonth,
                            title = "Streaks Overview",
                            subtitle = "View Detailed Reports",
                            onClick = onShowStreaksOverview
                        )
                    }

                    // Menu Item: Widget Options
                    item {
                        ProfileMenuCard(
                            icon = Icons.Filled.Widgets,
                            title = "Widget Options",
                            subtitle = "Streaks, Tasks & Motivation Widgets",
                            onClick = onShowWidgetOptions
                        )
                    }

                    item { Spacer(modifier = Modifier.height(10.dp)) }

                    // Welcome screen & Reset options
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onShowWelcome,
                                colors = ButtonDefaults.buttonColors(containerColor = SparkPeachLight),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Intro View", color = SparkFlame, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { showResetConfirmDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFAF2EE)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Reset Data", color = Color(0xFFB34A26), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Profile Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Account Details", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .border(2.dp, SparkOrange, CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.avatar_user),
                            contentDescription = "Profile Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Name", color = SparkTextSecondary) },
                        placeholder = { Text("e.g. Alex Morgan", color = Color(0xFFA59891)) },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color(0xFF1E140F),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF1E140F),
                            unfocusedTextColor = Color(0xFF1E140F),
                            focusedBorderColor = SparkOrange,
                            unfocusedBorderColor = SparkCardBorder,
                            focusedContainerColor = SparkPeachLight.copy(alpha = 0.35f),
                            unfocusedContainerColor = Color(0xFFFAF7F5),
                            cursorColor = SparkOrange
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("Email", color = SparkTextSecondary) },
                        placeholder = { Text("e.g. alex@example.com", color = Color(0xFFA59891)) },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color(0xFF1E140F),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF1E140F),
                            unfocusedTextColor = Color(0xFF1E140F),
                            focusedBorderColor = SparkOrange,
                            unfocusedBorderColor = SparkCardBorder,
                            focusedContainerColor = SparkPeachLight.copy(alpha = 0.35f),
                            unfocusedContainerColor = Color(0xFFFAF7F5),
                            cursorColor = SparkOrange
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateProfile(editName, editEmail, notificationsEnabled)
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SparkOrange)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = SparkTextSecondary)
                }
            }
        )
    }

    // Reset Demo Data Confirmation Dialog (DEF-008)
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text("Reset to Demo Data", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "This will restore starter demo habits and reset all completion progress and trophies. Are you sure you want to proceed?",
                    fontSize = 14.sp,
                    color = SparkTextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetDemoData()
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Reset", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Cancel", color = SparkTextSecondary)
                }
            }
        )
    }
}

@Composable
private fun ProfileMenuCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBF8)),
        border = BorderStroke(1.dp, SparkCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SparkTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = SparkTextSecondary
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFFA59891),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
