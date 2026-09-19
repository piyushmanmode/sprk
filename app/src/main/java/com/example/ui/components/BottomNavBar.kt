package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.SparkCardBorder
import com.example.ui.theme.SparkFlame
import com.example.ui.theme.SparkOrange
import com.example.ui.theme.SparkPeachLight

enum class NavTab {
    HOME,
    TROPHIES,
    CREATE,
    PROFILE
}

@Composable
fun SparkBottomNavBar(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = Color.White,
            shadowElevation = 10.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, SparkCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 1: Home / Flame
                NavIconItem(
                    isSelected = currentTab == NavTab.HOME,
                    onClick = { onTabSelected(NavTab.HOME) }
                ) {
                    SparkFlameIcon(
                        size = 24.dp,
                        tint = if (currentTab == NavTab.HOME) Color.White else SparkFlame
                    )
                }

                // Tab 2: Trophies
                NavIconItem(
                    isSelected = currentTab == NavTab.TROPHIES,
                    onClick = { onTabSelected(NavTab.TROPHIES) }
                ) {
                    Icon(
                        imageVector = if (currentTab == NavTab.TROPHIES) Icons.Filled.EmojiEvents else Icons.Outlined.EmojiEvents,
                        contentDescription = "Trophies",
                        tint = if (currentTab == NavTab.TROPHIES) Color.White else SparkFlame,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Tab 3: Plus / Create Habit
                NavIconItem(
                    isSelected = currentTab == NavTab.CREATE,
                    onClick = { onTabSelected(NavTab.CREATE) }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Create Streak",
                        tint = if (currentTab == NavTab.CREATE) Color.White else SparkFlame,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Tab 4: Profile
                NavIconItem(
                    isSelected = currentTab == NavTab.PROFILE,
                    onClick = { onTabSelected(NavTab.PROFILE) }
                ) {
                    Icon(
                        imageVector = if (currentTab == NavTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                        contentDescription = "Profile",
                        tint = if (currentTab == NavTab.PROFILE) Color.White else SparkFlame,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NavIconItem(
    isSelected: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(if (isSelected) SparkOrange else Color.Transparent)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
