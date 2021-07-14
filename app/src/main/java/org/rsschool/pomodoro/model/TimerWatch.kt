package org.rsschool.pomodoro.model

import android.os.CountDownTimer
import java.util.*

data class TimerWatch(
    val id: UUID,
    var untilFinishedMs: Long,
    var isStarted: Boolean
) {
    var periodTime = untilFinishedMs
    var position: Int? = null
    var countDownTimer: CountDownTimer? = null
}
