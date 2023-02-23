package com.mate.baedalmate.common

import android.content.Context
import com.mate.baedalmate.R
import java.time.Duration
import java.time.LocalDateTime
import java.time.Period

object TimeChangerUtil {
    fun getTimePassed(
        context: Context,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime
    ): String {
        var timePassed: String = ""
        Period.between(startDateTime.toLocalDate(), endDateTime.toLocalDate())
            .let { durationDate ->
                if (durationDate.months < 1) {
                    Duration.between(startDateTime, endDateTime).let { durationTime ->
                        timePassed = when {
                            durationTime.toDays() >= 1 -> "${durationTime.toDays()}${
                                context.getString(R.string.time_unit_days)
                            }"
                            durationTime.toHours() >= 1 -> "${durationTime.toHours()}${
                                context.getString(R.string.time_unit_hours)
                            }"
                            durationTime.toMinutes() >= 1 -> "${durationTime.toMinutes()}${
                                context.getString(R.string.time_unit_minutes)
                            }"
                            else -> "${durationTime.seconds}${context.getString(R.string.time_unit_seconds)}"
                        }
                    }
                } else {
                    timePassed = when {
                        durationDate.years >= 1 -> "${durationDate.years}${context.getString(R.string.time_unit_years)}"
                        else -> {
                            "${durationDate.months}${context.getString(R.string.time_unit_months)}"
                        }
                    }
                }
            }
        return timePassed
    }

    fun getTimeRemained(
        context: Context,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime
    ): String {
        var timeRemained: String = ""
        Period.between(startDateTime.toLocalDate(), endDateTime.toLocalDate())
            .let { durationDate ->
                if (durationDate.months < 1) {
                    Duration.between(startDateTime, endDateTime).let { durationTime ->
                        timeRemained = when {
                            durationTime.toDays() >= 1 -> "${durationTime.toDays()}${
                                context.getString(R.string.time_day)
                            }"
                            durationTime.toHours() >= 1 -> "${durationTime.toHours()}${
                                context.getString(R.string.time_hour)
                            }"
                            durationTime.toMinutes() >= 1 -> "${durationTime.toMinutes()}${
                                context.getString(R.string.time_minute)
                            }"
                            else -> "${durationTime.seconds}${context.getString(R.string.time_second)}"
                        }
                    }
                } else {
                    timeRemained = when {
                        durationDate.years >= 1 -> "${durationDate.years}${context.getString(R.string.time_year)}"
                        else -> {
                            "${durationDate.months}${context.getString(R.string.time_month)}"
                        }
                    }
                }
            }
        return timeRemained
    }
}