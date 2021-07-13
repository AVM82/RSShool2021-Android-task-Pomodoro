package org.rsschool.pomodoro.ui.adapters

import android.graphics.drawable.AnimationDrawable
import android.media.RingtoneManager
import android.net.Uri
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import org.rsschool.pomodoro.R
import org.rsschool.pomodoro.databinding.StopwatchItemBinding
import org.rsschool.pomodoro.extension.UNIT_TEN_MS
import org.rsschool.pomodoro.extension.displayTime
import org.rsschool.pomodoro.extension.resetTime
import org.rsschool.pomodoro.model.TimerWatch
import org.rsschool.pomodoro.ui.StopWatchListener


class ViewHolder(
    private var binding: StopwatchItemBinding,
    private var listener: StopWatchListener
) : RecyclerView.ViewHolder(binding.root) {

    private val context = binding.root.context
    private val resources = context.resources

    fun bind(timerWatch: TimerWatch) {
        binding.stopwatchTimer.text = timerWatch.currentMs.displayTime()
        timerWatch.position = adapterPosition
        if (timerWatch.isStarted) {
            startTimer(timerWatch)
        } else {
            stopTimer(timerWatch)
        }
        initButtonListeners(timerWatch)
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
        timerWatch.countDownTimer = getCountdownTimer(timerWatch)
        timerWatch.countDownTimer?.start()
    }

    private fun initButtonListeners(timerWatch: TimerWatch) {

        binding.deleteItemButton.setOnClickListener {
            timerWatch.countDownTimer?.cancel()
            listener.delete(id = timerWatch.id, adapterPosition)
        }

        if (timerWatch.isStarted) {
            binding.restartButton.setOnClickListener {
                timerWatch.countDownTimer?.cancel()
                listener.stop(
                    timerWatch = timerWatch,
                    position = adapterPosition
                )
            }
        } else {
            binding.restartButton.setOnClickListener {
                listener.start(
                    timerWatch = timerWatch,
                    position = adapterPosition
                )
            }
        }
    }

    private fun getCountdownTimer(timerWatch: TimerWatch): CountDownTimer {
        return object : CountDownTimer(timerWatch.currentMs, UNIT_TEN_MS) {
            override fun onTick(millisUntilFinished: Long) {
                timerWatch.currentMs = millisUntilFinished
                if (adapterPosition == timerWatch.position) {
                    binding.stopwatchTimer.text =
                        timerWatch.currentMs.displayTime()
                }
            }

            override fun onFinish() {
                binding.stopwatchTimer.text = timerWatch.startTime.displayTime()
                timeOverNotify()
                timerWatch.resetTime()
                stopTimer(timerWatch)
                listener.stop(timerWatch = timerWatch, position = adapterPosition)
            }

            private fun timeOverNotify() {
                showMessage()
                playSound()
            }

            private fun showMessage() {
                Toast.makeText(context, "Time is over", Toast.LENGTH_SHORT).show()
            }

            private fun playSound() {
                try {
                    val notify: Uri =
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    val r = RingtoneManager.getRingtone(
                        context,
                        notify
                    )
                    r.play()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
