package org.rsschool.pomodoro.model

import java.util.*

data class StopWatch(
    val id: UUID,
    var currentMs: Long,
    var isStarted: Boolean
)
