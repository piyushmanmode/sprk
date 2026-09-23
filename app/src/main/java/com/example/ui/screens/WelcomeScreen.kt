package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SparkFlameIcon
import com.example.ui.theme.SparkCardBorder
import com.example.ui.theme.SparkOrange
import com.example.ui.theme.SparkPeachLight

@Composable
fun WelcomeScreen(
    onGetStarted: (name: String, email: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFF9E3D),
                        Color(0xFFFF6D00),
                        Color(0xFFFF4E11),
                        Color(0xFFE63900)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top branding
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        SparkFlameIcon(size = 20.dp, tint = Color.White)
                    }
                    Text(
                        text = "Welcome to Spark",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Build Streaks\n& Track Habits",
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 40.sp
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Set up your profile to start tracking your daily achievements and unlock flame trophies.",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // User Profile Input Card
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, SparkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Tell Us About You",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E140F)
                    )
                    Text(
                        text = "Enter your details to personalize your streaks",
                        fontSize = 12.sp,
                        color = Color(0xFF756A63)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Name Input
                    Text(
                        text = "Your Name",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E140F)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        placeholder = { Text("e.g. Alex Morgan", color = Color(0xFFA59891)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Name",
                                tint = SparkOrange
                            )
                        },
                        textStyle = TextStyle(
                            color = Color(0xFF1E140F),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF1E140F),
                            unfocusedTextColor = Color(0xFF1E140F),
                            focusedBorderColor = SparkOrange,
                            unfocusedBorderColor = SparkCardBorder,
                            focusedContainerColor = SparkPeachLight.copy(alpha = 0.35f),
                            unfocusedContainerColor = Color(0xFFFAF7F5),
                            cursorColor = SparkOrange
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("welcome_name_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Email Input
                    Text(
                        text = "Your Email",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E140F)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        placeholder = { Text("e.g. alex@example.com", color = Color(0xFFA59891)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Email,
                                contentDescription = "Email",
                                tint = SparkOrange
                            )
                        },
                        textStyle = TextStyle(
                            color = Color(0xFF1E140F),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF1E140F),
                            unfocusedTextColor = Color(0xFF1E140F),
                            focusedBorderColor = SparkOrange,
                            unfocusedBorderColor = SparkCardBorder,
                            focusedContainerColor = SparkPeachLight.copy(alpha = 0.35f),
                            unfocusedContainerColor = Color(0xFFFAF7F5),
                            cursorColor = SparkOrange
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("welcome_email_input")
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom CTA Button: Pill with Flame icon and "Get Started >>>"
            Button(
                onClick = {
                    val finalName = if (nameInput.isNotBlank()) nameInput.trim() else "Streak Champion"
                    val finalEmail = emailInput.trim()
                    onGetStarted(finalName, finalEmail)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .testTag("get_started_button")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SparkPeachLight),
                        contentAlignment = Alignment.Center
                    ) {
                        SparkFlameIcon(size = 20.dp, tint = SparkOrange)
                    }

                    Text(
                        text = "Get Started",
                        color = Color(0xFF1E140F),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Continue",
                        tint = SparkOrange,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
