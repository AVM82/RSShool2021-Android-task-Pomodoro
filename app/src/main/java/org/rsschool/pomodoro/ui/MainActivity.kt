package org.rsschool.pomodoro.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.rsschool.pomodoro.databinding.ActivityMainBinding
import org.rsschool.pomodoro.model.TimerWatch
import org.rsschool.pomodoro.ui.adapters.StopWatchListAdapter
import java.util.*

class MainActivity : AppCompatActivity(), StopWatchListener {
    private lateinit var binding: ActivityMainBinding
    private val stopWatchListAdapter = StopWatchListAdapter(this)
    private val stopWatchList = mutableListOf<TimerWatch>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.timersList.adapter = stopWatchListAdapter


        binding.addTimerButton.setOnClickListener {
            val minutes = binding.textEditMinutes.text.toString().toLongOrNull()
            val stopWatch =
                TimerWatch(UUID.randomUUID(), minutes?.let { minutes * 60000L } ?: 0L, false)
            stopWatchList.add(stopWatch)
            stopWatchListAdapter.submitList(stopWatchList)
            stopWatchListAdapter.notifyItemInserted(stopWatchList.size - 1)
        }

    }

    override fun delete(id: UUID) {
        stopWatchList.remove(stopWatchList.find { it.id == id })
        stopWatchListAdapter.submitList(stopWatchList)
        stopWatchListAdapter.notifyDataSetChanged()
    }

    override fun start(timerWatch: TimerWatch, position: Int) {
        changeTimerWatch(timerWatch = timerWatch, isStarted = true, position = position)
    }

    override fun stop(timerWatch: TimerWatch, position: Int) {
        changeTimerWatch(timerWatch = timerWatch, isStarted = false, position = position)
    }

    private fun changeTimerWatch(timerWatch: TimerWatch, isStarted: Boolean, position: Int) {
        stopWatchList.find { it.id == timerWatch.id }?.isStarted = isStarted
        stopWatchListAdapter.notifyItemChanged(position)
    }
}