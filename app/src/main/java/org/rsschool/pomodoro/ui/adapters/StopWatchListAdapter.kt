package org.rsschool.pomodoro.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import org.rsschool.pomodoro.databinding.StopwatchItemBinding
import org.rsschool.pomodoro.model.TimerWatch
import org.rsschool.pomodoro.ui.StopWatchListener

class StopWatchListAdapter(private val listener: StopWatchListener) :
    ListAdapter<TimerWatch, ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<TimerWatch>() {
        override fun areItemsTheSame(oldItem: TimerWatch, newItem: TimerWatch): Boolean {
            return oldItem.id == newItem.id

        }

        override fun areContentsTheSame(oldItem: TimerWatch, newItem: TimerWatch): Boolean {
            return oldItem.currentMs == newItem.currentMs &&
                    oldItem.isStarted == newItem.isStarted
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = StopwatchItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
