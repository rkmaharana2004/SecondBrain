package com.rkproduction.secondbrain.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun StreakHeatmap(
    activity: Map<LocalDate, Int>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val emptyColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    
    val today = LocalDate.now()
    val currentMonth = YearMonth.from(today)
    val daysInMonth = currentMonth.lengthOfMonth()
    
    val columns = 7
    val rows = (daysInMonth + columns - 1) / columns

    Column(modifier = modifier) {
        Text(
            text = currentMonth.month.name,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        
        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(rows) { rowIndex ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(columns) { colIndex ->
                        val dayIndex = rowIndex * columns + colIndex
                        val dayNumber = dayIndex + 1
                        
                        if (dayNumber <= daysInMonth) {
                            val date = currentMonth.atDay(dayNumber)
                            val count = activity[date] ?: 0
                            
                            val backgroundColor = when {
                                count >= 5 -> primaryColor
                                count >= 3 -> primaryColor.copy(alpha = 0.7f)
                                count >= 1 -> primaryColor.copy(alpha = 0.4f)
                                date.isAfter(today) -> emptyColor.copy(alpha = 0.1f)
                                else -> emptyColor
                            }

                            val contentColor = when {
                                count >= 3 -> onPrimaryColor
                                count >= 1 -> MaterialTheme.colorScheme.onSurface
                                else -> MaterialTheme.colorScheme.outline
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(backgroundColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayNumber.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = contentColor
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                        }
                    }
                }
            }
        }
    }
}
