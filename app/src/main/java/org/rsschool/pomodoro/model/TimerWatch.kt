package org.rsschool.pomodoro.model

import android.os.CountDownTimer
import java.util.*

data class TimerWatch(
    val id: UUID,
    var currentMs: Long,
    var isStarted: Boolean
) {
    var startTime = currentMs
    var countDownTimer: CountDownTimer? = null
}
