package org.rsschool.pomodoro.ui.adapters

import android.graphics.drawable.AnimationDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.rsschool.pomodoro.R
import org.rsschool.pomodoro.databinding.StopwatchItemBinding
import org.rsschool.pomodoro.model.TimerWatch
import org.rsschool.pomodoro.ui.StopWatchListener

class StopWatchListAdapter(private val listener: StopWatchListener) :
    ListAdapter<TimerWatch, StopWatchListAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<TimerWatch>() {
        override fun areItemsTheSame(oldItem: TimerWatch, newItem: TimerWatch): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: TimerWatch, newItem: TimerWatch): Boolean {
            return oldItem.id == newItem.id
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = StopwatchItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, listener)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

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
            binding.blinkingIndicator.visibility = View.INVISIBLE
            (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
            binding.restartButton.text = resources.getString(R.string.start_timer_button_text)
        }

        private fun startTimer() {
            binding.restartButton.text = resources.getString(R.string.stop_timer_button_text)
            binding.blinkingIndicator.isVisible = true
            (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
        }

        private fun initButtonListeners(timerWatch: TimerWatch, position: Int) {
            binding.deleteItemButton.setOnClickListener {
                listener.delete(timerWatch.id)

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
}
