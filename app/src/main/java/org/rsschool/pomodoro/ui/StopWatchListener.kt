package org.rsschool.pomodoro.ui

import org.rsschool.pomodoro.model.TimerWatch
import java.util.*

interface StopWatchListener {

    fun delete(id: UUID)

    fun start(timerWatch: TimerWatch, position: Int)

    fun stop(timerWatch: TimerWatch, position: Int)

}