package org.rsschool.pomodoro.ui.adapters

import android.graphics.drawable.AnimationDrawable
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import org.rsschool.pomodoro.R
import org.rsschool.pomodoro.databinding.StopwatchItemBinding
import org.rsschool.pomodoro.extension.displayTime
import org.rsschool.pomodoro.model.TimerWatch
import org.rsschool.pomodoro.timer.getCountdownTimer
import org.rsschool.pomodoro.ui.StopWatchListener

class ViewHolder(
    private var binding: StopwatchItemBinding,
    private var listener: StopWatchListener
) : RecyclerView.ViewHolder(binding.root) {

    private val resources = binding.root.context.resources

    fun bind(timerWatch: TimerWatch, position: Int) {
        binding.stopwatchTimer.text = timerWatch.currentMs.displayTime()
        if (timerWatch.isStarted) {
            startTimer(timerWatch)
        } else {
            stopTimer(timerWatch)
        }
        initButtonListeners(timerWatch, position)
    }

    private fun stopTimer(timerWatch: TimerWatch) {
        binding.apply {
            blinkingIndicator.visibility = View.INVISIBLE
            (blinkingIndicator.background as? AnimationDrawable)?.start()
            restartButton.text = resources.getString(R.string.start_timer_button_text)
        }
        timerWatch.countDownTimer?.cancel()

    }

    private fun startTimer(timerWatch: TimerWatch) {
        binding.apply {
            blinkingIndicator.isVisible = true
            restartButton.text = resources.getString(R.string.stop_timer_button_text)
            (blinkingIndicator.background as? AnimationDrawable)?.start()
        }
        timerWatch.countDownTimer?.cancel()
        timerWatch.countDownTimer =
            getCountdownTimer(timerWatch, binding) { stopTimer(timerWatch) }.start()
//        timer = getCountdownTimer(timerWatch, binding) {stopTimer()}
        timerWatch.countDownTimer?.start()
    }

    private fun initButtonListeners(timerWatch: TimerWatch, position: Int) {

        Log.d("position -> ", position.toString())
        Log.d("adapterPosition -> ", adapterPosition.toString())

        binding.deleteItemButton.setOnClickListener {
            timerWatch.countDownTimer?.cancel()
            listener.delete(id = timerWatch.id, position = position)
        }

        if (timerWatch.isStarted) {
            binding.restartButton.setOnClickListener {
//                timer?.cancel()
                listener.stop(
                    timerWatch = timerWatch,
                    position = position
                )
            }
        } else {
            binding.restartButton.setOnClickListener {
                listener.start(
                    timerWatch = timerWatch,
                    position = position
                )
            }
        }
    }

//    private fun getCountdownTimer(timerWatch: TimerWatch): CountDownTimer {
//        return object : CountDownTimer(timerWatch.currentMs, UNIT_TEN_MS) {
//            override fun onTick(millisUntilFinished: Long) {
//                timerWatch.currentMs = millisUntilFinished
//                Log.d("on tick", timerWatch.currentMs.displayTime())
//                Log.d("on tick until finished", millisUntilFinished.displayTime())
//                binding.stopwatchTimer.text =
//                    timerWatch.currentMs.displayTime()
//            }
//
//            override fun onFinish() {
//                binding.stopwatchTimer.text = timerWatch.startTime.displayTime()
//                timerWatch.resetTime()
//                stopTimer()
//            }
//        }
//    }


}


