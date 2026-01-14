package com.se122.interactivelearning.ui.components.shapes

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*

class HexagonShape(
    private val cornerRadius: Float = 12f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val width = size.width
        val height = size.height
        val triangleHeight = (sqrt(3f) / 2f) * width / 2f
        val r = cornerRadius.coerceAtMost(min(width, height) / 4f)

        val points = listOf(
            Offset(width / 2f, 0f),
            Offset(width, triangleHeight),
            Offset(width, height - triangleHeight),
            Offset(width / 2f, height),
            Offset(0f, height - triangleHeight),
            Offset(0f, triangleHeight)
        )

        val path = Path().apply {
            moveTo(points[0].x, points[0].y + r)

            for (i in 0 until 6) {
                val current = points[i]
                val next = points[(i + 1) % 6]
                val angle = atan2(next.y - current.y, next.x - current.x)
                val offsetX = r * cos(angle)
                val offsetY = r * sin(angle)

                lineTo(next.x - offsetX, next.y - offsetY)
                arcTo(
                    rect = Rect(
                        left = next.x - r,
                        top = next.y - r,
                        right = next.x + r,
                        bottom = next.y + r
                    ),
                    startAngleDegrees = angle * 180f / PI.toFloat() - 90f,
                    sweepAngleDegrees = 60f,
                    forceMoveTo = false
                )
            }
            close()
        }
        return Outline.Generic(path)
    }
}