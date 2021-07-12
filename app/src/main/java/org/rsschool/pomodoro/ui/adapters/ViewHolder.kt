package org.rsschool.pomodoro.ui.adapters

import android.graphics.drawable.AnimationDrawable
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import org.rsschool.pomodoro.R
import org.rsschool.pomodoro.databinding.StopwatchItemBinding
import org.rsschool.pomodoro.model.TimerWatch
import org.rsschool.pomodoro.ui.StopWatchListener

class ViewHolder(
    private var binding: StopwatchItemBinding,
    private var listener: StopWatchListener
) : RecyclerView.ViewHolder(binding.root) {

    private val resources = binding.root.context.resources

    fun bind(timerWatch: TimerWatch, position: Int) {
        binding.stopwatchTimer.text = timerWatch.currentMs.displayTime()

        if (timerWatch.isStarted) {
            startTimer()
        } else {
            stopTimer()
        }
        initButtonListeners(timerWatch, position)
    }

    private fun stopTimer() {
        binding.apply {
            blinkingIndicator.visibility = View.INVISIBLE
            (blinkingIndicator.background as? AnimationDrawable)?.start()
            restartButton.text = resources.getString(R.string.start_timer_button_text)
        }
    }

    private fun startTimer() {
        binding.apply {
            blinkingIndicator.isVisible = true
            restartButton.text = resources.getString(R.string.stop_timer_button_text)
            (blinkingIndicator.background as? AnimationDrawable)?.start()
        }
    }

    private fun initButtonListeners(timerWatch: TimerWatch, position: Int) {
        binding.deleteItemButton.setOnClickListener {
            listener.delete(id = timerWatch.id, position = position)
        }

        if (timerWatch.isStarted) {
            binding.restartButton.setOnClickListener {
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

    private fun Long.displayTime(): String {
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

    companion object {
        private const val END_TIME = "00:00:00"
    }
}