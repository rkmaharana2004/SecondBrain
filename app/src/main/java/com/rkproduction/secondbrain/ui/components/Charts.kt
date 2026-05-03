package com.rkproduction.secondbrain.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SimpleBarChart(
    data: Map<String, Int>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary
) {
    var animationTriggered by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationTriggered) 1f else 0f,
        animationSpec = tween(1000)
    )

    LaunchedEffect(Unit) { animationTriggered = true }

    Canvas(modifier = modifier) {
        val maxCount = data.values.maxOrNull()?.toFloat() ?: 1f
        val barWidth = size.width / (data.size * 2)
        val spacing = size.width / (data.size * 2)

        data.values.forEachIndexed { index, count ->
            val barHeight = (count / maxCount) * size.height * animatedProgress
            val x = (index * (barWidth + spacing)) + spacing
            val y = size.height - barHeight

            drawRect(
                color = barColor,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight)
            )
        }
    }
}
