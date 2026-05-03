package com.rkproduction.secondbrain.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import kotlin.math.max

/**
 * Custom Flow Layout for the Tag Cloud
 */
@Composable
fun TagCloudLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0)) }
        
        var layoutWidth = 0
        var layoutHeight = 0
        var currentX = 0
        var currentY = 0
        var currentRowHeight = 0

        val positions = placeables.map { placeable ->
            if (currentX + placeable.width > constraints.maxWidth) {
                currentX = 0
                currentY += currentRowHeight + 16 // spacing
                currentRowHeight = 0
            }
            
            val pos = androidx.compose.ui.unit.IntOffset(currentX, currentY)
            currentX += placeable.width + 16 // spacing
            currentRowHeight = max(currentRowHeight, placeable.height)
            layoutWidth = max(layoutWidth, currentX)
            layoutHeight = max(layoutHeight, currentY + currentRowHeight)
            pos
        }

        layout(constraints.maxWidth, layoutHeight) {
            placeables.forEachIndexed { index, placeable ->
                placeable.placeRelative(positions[index])
            }
        }
    }
}
