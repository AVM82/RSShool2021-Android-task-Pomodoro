package org.rsschool.pomodoro.model

import java.util.*

data class TimerWatch(
    val id: UUID,
    var currentMs: Long,
    var isStarted: Boolean
) {
    var startTime = currentMs
}
