package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.SparkFlame
import com.example.ui.theme.SparkOrange
import com.example.ui.theme.SparkWarmAmber

@Composable
fun SparkFlameIcon(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color? = null,
    showGlow: Boolean = false
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            val brush = if (tint != null) {
                Brush.linearGradient(listOf(tint, tint))
            } else {
                Brush.verticalGradient(
                    colors = listOf(SparkWarmAmber, SparkFlame, SparkOrange),
                    startY = 0f,
                    endY = h
                )
            }

            // Outer flame contour
            val outerPath = Path().apply {
                moveTo(w * 0.5f, 0f)
                cubicTo(
                    w * 0.55f, h * 0.22f,
                    w * 0.85f, h * 0.35f,
                    w * 0.90f, h * 0.62f
                )
                cubicTo(
                    w * 0.95f, h * 0.85f,
                    w * 0.75f, h * 1.0f,
                    w * 0.50f, h * 1.0f
                )
                cubicTo(
                    w * 0.25f, h * 1.0f,
                    w * 0.05f, h * 0.85f,
                    w * 0.10f, h * 0.62f
                )
                cubicTo(
                    w * 0.15f, h * 0.40f,
                    w * 0.38f, h * 0.28f,
                    w * 0.45f, h * 0.15f
                )
                close()
            }

            // Inner flame contour
            val innerPath = Path().apply {
                moveTo(w * 0.50f, h * 0.45f)
                cubicTo(
                    w * 0.60f, h * 0.58f,
                    w * 0.68f, h * 0.70f,
                    w * 0.65f, h * 0.82f
                )
                cubicTo(
                    w * 0.62f, h * 0.92f,
                    w * 0.50f, h * 0.94f,
                    w * 0.48f, h * 0.94f
                )
                cubicTo(
                    w * 0.38f, h * 0.92f,
                    w * 0.35f, h * 0.82f,
                    w * 0.38f, h * 0.70f
                )
                cubicTo(
                    w * 0.40f, h * 0.60f,
                    w * 0.48f, h * 0.52f,
                    w * 0.50f, h * 0.45f
                )
                close()
            }

            // Draw outer flame stroke or fill
            drawPath(
                path = outerPath,
                brush = brush,
                style = Stroke(
                    width = w * 0.09f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Draw inner flame
            drawPath(
                path = innerPath,
                brush = brush,
                style = Stroke(
                    width = w * 0.08f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}
