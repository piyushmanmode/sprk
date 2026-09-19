package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.SparkActiveDot
import com.example.ui.theme.SparkInactiveDot
import kotlin.random.Random

@Composable
fun DotMatrixHeatmap(
    modifier: Modifier = Modifier,
    completedDaysCount: Int = 27,
    totalSlots: Int = 36,
    rows: Int = 3,
    dotSize: Dp = 6.dp,
    dotSpacing: Dp = 4.dp,
    activeColor: Color = SparkActiveDot,
    inactiveColor: Color = SparkInactiveDot,
    seed: Long = 42L
) {
    val cols = (totalSlots + rows - 1) / rows

    // Pre-calculate which dots are active based on completed count and pseudo-random natural clustering
    val dotStates = BooleanArray(totalSlots) { index ->
        if (index < completedDaysCount) true else false
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dotSpacing)
    ) {
        for (r in 0 until rows) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(dotSpacing)
            ) {
                for (c in 0 until cols) {
                    val index = r * cols + c
                    val isActive = if (index < totalSlots) dotStates[index] else false
                    Box(
                        modifier = Modifier
                            .size(dotSize)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (isActive) activeColor else inactiveColor)
                    )
                }
            }
        }
    }
}
