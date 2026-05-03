package com.rkproduction.secondbrain.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class StreakCalculator {

    fun calculateCurrentStreak(timestamps: List<Long>): Int {
        if (timestamps.isEmpty()) return 0
        
        val dates = timestamps.map { 
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() 
        }.distinct().sortedDescending()

        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        
        if (dates.first() != today && dates.first() != yesterday) return 0

        var streak = 0
        var currentCheck = dates.first()

        for (date in dates) {
            if (date == currentCheck) {
                streak++
                currentCheck = currentCheck.minusDays(1)
            } else {
                break
            }
        }
        return streak
    }

    fun calculateLongestStreak(timestamps: List<Long>): Int {
        if (timestamps.isEmpty()) return 0
        
        val dates = timestamps.map { 
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() 
        }.distinct().sorted()

        var maxStreak = 0
        var currentStreak = 0
        var lastDate: LocalDate? = null

        for (date in dates) {
            if (lastDate == null || ChronoUnit.DAYS.between(lastDate, date) == 1L) {
                currentStreak++
            } else if (ChronoUnit.DAYS.between(lastDate, date) > 1L) {
                currentStreak = 1
            }
            maxStreak = maxOf(maxStreak, currentStreak)
            lastDate = date
        }
        return maxStreak
    }
}
