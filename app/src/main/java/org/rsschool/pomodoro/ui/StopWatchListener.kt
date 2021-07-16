package org.rsschool.pomodoro.ui

import android.os.CountDownTimer
import org.rsschool.pomodoro.model.TimerWatch
import java.util.*

interface StopWatchListener {

    fun delete(id: UUID, position: Int)

    fun start(timerWatch: TimerWatch, position: Int)

    fun stop(timerWatch: TimerWatch, position: Int)

    fun setTimer(timer: CountDownTimer, id: UUID)

}