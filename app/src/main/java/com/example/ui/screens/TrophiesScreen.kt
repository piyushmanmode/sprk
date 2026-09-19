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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Trophy
import com.example.ui.components.SparkFlameIcon
import com.example.ui.theme.SparkCardBorder
import com.example.ui.theme.SparkFlame
import com.example.ui.theme.SparkOrange
import com.example.ui.theme.SparkPeachLight
import com.example.ui.theme.SparkTextPrimary
import com.example.ui.theme.SparkTextSecondary
import com.example.ui.theme.SparkTrophyGold

@Composable
fun TrophiesScreen(
    trophies: List<Trophy>,
    onTrophyClick: (Trophy) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val unlockedCount = trophies.count { it.isUnlocked }

    // Group trophies by habit title or category
    val grouped = trophies.groupBy { it.habitTitle ?: "General Milestones" }

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
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Trophies",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // Top right count badge and actions
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Flame + Count badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.25f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        SparkFlameIcon(size = 14.dp, tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%02d", unlockedCount),
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

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
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    grouped.forEach { (habitTitle, trophyList) ->
                        item(key = habitTitle) {
                            Column {
                                // Section header row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = habitTitle,
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
                                            text = String.format("%02d", sectionUnlocked),
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
                                        TrophyCard(
                                            trophy = trophy,
                                            onClick = {
                                                if (trophy.isUnlocked) {
                                                    onTrophyClick(trophy)
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "Locked: Requires ${trophy.requiredDays} continuous streak days!",
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
                                        trophyList.drop(2).forEach { trophy ->
                                            TrophyCard(
                                                trophy = trophy,
                                                onClick = {
                                                    if (trophy.isUnlocked) {
                                                        onTrophyClick(trophy)
                                                    } else {
                                                        Toast.makeText(
                                                            context,
                                                            "Locked: Requires ${trophy.requiredDays} continuous streak days!",
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

@Composable
private fun TrophyCard(
    trophy: Trophy,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon / Trophy Visual
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(if (trophy.isUnlocked) SparkPeachLight else Color(0xFFEDE5DF)),
                contentAlignment = Alignment.Center
            ) {
                if (trophy.isUnlocked) {
                    Image(
                        painter = painterResource(id = R.drawable.trophy_gold_badge),
                        contentDescription = trophy.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(54.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Locked",
                        tint = Color(0xFFA59891),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Status Badge (Unlocked / Locked)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (trophy.isUnlocked) SparkOrange else Color(0xFFA59891))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = if (trophy.isUnlocked) "Unlocked" else "Locked",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = trophy.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (trophy.isUnlocked) SparkTextPrimary else Color(0xFFA59891),
                textAlign = TextAlign.Center
            )
        }
    }
}
