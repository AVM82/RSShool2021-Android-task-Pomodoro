package org.rsschool.pomodoro.timer

import android.os.CountDownTimer
import android.util.Log
import org.rsschool.pomodoro.databinding.StopwatchItemBinding
import org.rsschool.pomodoro.extension.UNIT_TEN_MS
import org.rsschool.pomodoro.extension.displayTime
import org.rsschool.pomodoro.extension.resetTime
import org.rsschool.pomodoro.model.TimerWatch

fun getCountdownTimer(
    timerWatch: TimerWatch,
    binding: StopwatchItemBinding,
    onFinished: () -> Unit
): CountDownTimer {

    return object : CountDownTimer(timerWatch.currentMs, UNIT_TEN_MS) {
        override fun onTick(millisUntilFinished: Long) {
            timerWatch.currentMs = millisUntilFinished
            Log.d("on tick", timerWatch.currentMs.displayTime())
            Log.d("on tick until finished", millisUntilFinished.displayTime())
            binding.stopwatchTimer.text = timerWatch.currentMs.displayTime()
        }

        override fun onFinish() {
            binding.stopwatchTimer.text = timerWatch.startTime.displayTime()
            timerWatch.resetTime()
            onFinished()
        }
    }
}
