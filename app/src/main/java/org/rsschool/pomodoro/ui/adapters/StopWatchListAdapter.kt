package org.rsschool.pomodoro.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.rsschool.pomodoro.databinding.StopwatchItemBinding
import org.rsschool.pomodoro.model.StopWatch

class StopWatchListAdapter :
    ListAdapter<StopWatch, StopWatchListAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<StopWatch>() {
        override fun areItemsTheSame(oldItem: StopWatch, newItem: StopWatch): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: StopWatch, newItem: StopWatch): Boolean {
            return oldItem.id == newItem.id
        }

    }

    class ViewHolder(var binding: StopwatchItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(stopWatch: StopWatch) {
            binding.stopwatchTimer.text = stopWatch.currentMs.displayTime()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = StopwatchItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private fun Long.displayTime(): String {
    return "00:00:00"
}
