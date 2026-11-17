package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Draws a realistic metallic gold EMV Smart Chip for the ATM health card.
 */
@Composable
fun EmvChip(
    modifier: Modifier = Modifier,
    width: Dp = 46.dp,
    height: Dp = 36.dp
) {
    Canvas(modifier = modifier.size(width, height)) {
        val w = size.width
        val h = size.height

        // Gold gradient background
        val goldBrush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFE082),
                Color(0xFFFFB300),
                Color(0xFFFFD54F),
                Color(0xFFFFA000)
            ),
            start = Offset(0f, 0f),
            end = Offset(w, h)
        )

        // Chip body
        drawRoundRect(
            brush = goldBrush,
            size = Size(w, h),
            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
        )

        // Chip outer border
        drawRoundRect(
            color = Color(0xFFB8860B),
            size = Size(w, h),
            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
            style = Stroke(width = 1.2.dp.toPx())
        )

        // Circuit lines (Microchip contact pads)
        val lineColor = Color(0xFF8D6E63)
        val lineStroke = Stroke(width = 1.dp.toPx(), cap = StrokeCap.Round)

        // Horizontal dividing line
        drawLine(
            color = lineColor,
            start = Offset(0f, h * 0.5f),
            end = Offset(w, h * 0.5f),
            strokeWidth = lineStroke.width
        )

        // Left vertical line
        drawLine(
            color = lineColor,
            start = Offset(w * 0.3f, 0f),
            end = Offset(w * 0.3f, h),
            strokeWidth = lineStroke.width
        )

        // Right vertical line
        drawLine(
            color = lineColor,
            start = Offset(w * 0.7f, 0f),
            end = Offset(w * 0.7f, h),
            strokeWidth = lineStroke.width
        )

        // Central contact pad
        drawRoundRect(
            color = Color(0xFFFFECB3),
            topLeft = Offset(w * 0.35f, h * 0.28f),
            size = Size(w * 0.3f, h * 0.44f),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
        )
        drawRoundRect(
            color = lineColor,
            topLeft = Offset(w * 0.35f, h * 0.28f),
            size = Size(w * 0.3f, h * 0.44f),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

/**
 * Draws the contactless NFC wave symbol seen on modern ATM/Health cards.
 */
@Composable
fun ContactlessWaves(
    modifier: Modifier = Modifier,
    color: Color = Color.White.copy(alpha = 0.85f),
    size: Dp = 22.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val s = this.size.minDimension
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)

        // 3 concentric arcs for contactless radio wave
        val center = Offset(s * 0.2f, s * 0.5f)

        for (i in 1..3) {
            val radius = (s * 0.24f * i)
            drawArc(
                color = color,
                startAngle = -50f,
                sweepAngle = 100f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = stroke
            )
        }
    }
}

/**
 * Renders a stylized QR code block for card back or verification
 */
@Composable
fun HealthQrCode(
    modifier: Modifier = Modifier,
    seed: String = "HP-HEALTH-CARD",
    size: Dp = 72.dp,
    backgroundColor: Color = Color.White,
    darkColor: Color = Color(0xFF1E293B)
) {
    Canvas(modifier = modifier.size(size)) {
        val s = this.size.minDimension
        val grid = 17
        val cellSize = s / grid

        // Background
        drawRect(color = backgroundColor, size = Size(s, s))

        // Deterministic pseudo-pattern based on seed string hash
        val hash = seed.hashCode()
        val random = java.util.Random(hash.toLong())

        // Finder patterns (3 corners: top-left, top-right, bottom-left)
        fun drawFinder(gx: Int, gy: Int) {
            // Outer 7x7
            drawRect(
                color = darkColor,
                topLeft = Offset(gx * cellSize, gy * cellSize),
                size = Size(7 * cellSize, 7 * cellSize)
            )
            // Inner white 5x5
            drawRect(
                color = backgroundColor,
                topLeft = Offset((gx + 1) * cellSize, (gy + 1) * cellSize),
                size = Size(5 * cellSize, 5 * cellSize)
            )
            // Center dark 3x3
            drawRect(
                color = darkColor,
                topLeft = Offset((gx + 2) * cellSize, (gy + 2) * cellSize),
                size = Size(3 * cellSize, 3 * cellSize)
            )
        }

        drawFinder(0, 0)
        drawFinder(grid - 7, 0)
        drawFinder(0, grid - 7)

        // Timing patterns
        for (i in 8 until grid - 8) {
            if (i % 2 == 0) {
                drawRect(
                    color = darkColor,
                    topLeft = Offset(6 * cellSize, i * cellSize),
                    size = Size(cellSize, cellSize)
                )
                drawRect(
                    color = darkColor,
                    topLeft = Offset(i * cellSize, 6 * cellSize),
                    size = Size(cellSize, cellSize)
                )
            }
        }

        // Data cells
        for (r in 0 until grid) {
            for (c in 0 until grid) {
                val inTL = r < 8 && c < 8
                val inTR = r < 8 && c >= grid - 8
                val inBL = r >= grid - 8 && c < 8
                val inTiming = r == 6 || c == 6
                if (!inTL && !inTR && !inBL && !inTiming) {
                    if (random.nextBoolean()) {
                        drawRect(
                            color = darkColor,
                            topLeft = Offset(c * cellSize, r * cellSize),
                            size = Size(cellSize, cellSize)
                        )
                    }
                }
            }
        }
    }
}
