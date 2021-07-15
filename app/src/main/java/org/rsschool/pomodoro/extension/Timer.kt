package org.rsschool.pomodoro.extension

import org.rsschool.pomodoro.model.TimerWatch
import java.util.*

private const val END_TIME = "00:00:00"
const val UNIT_TEN_MS = 1000L



var activeTimerId: UUID? = null

fun TimerWatch.resetTime() {
    this.untilFinishedMs = this.periodTime
}

fun Long.displayTime(): String {
    if (this <= 0L) {
        return END_TIME
    }
    val h = this / 1000 / 3600
    val m = this / 1000 % 3600 / 60
    val s = this / 1000 % 60
    return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s)}"
}

private fun displaySlot(count: Long): String {
    return if (count / 10L > 0) {
        "$count"
    } else {
        "0$count"
    }
}

