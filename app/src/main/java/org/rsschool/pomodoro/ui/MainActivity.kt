package org.rsschool.pomodoro.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.rsschool.pomodoro.databinding.ActivityMainBinding
import org.rsschool.pomodoro.model.StopWatch
import org.rsschool.pomodoro.ui.adapters.StopWatchListAdapter
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val stopWatchListAdapter = StopWatchListAdapter()
    private val stopWatchList = mutableListOf<StopWatch>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.timersList.adapter = stopWatchListAdapter


        binding.addTimerButton.setOnClickListener {
            val minutes = binding.textEditMinutes.text.toString().toLongOrNull()
            val stopWatch =
                StopWatch(UUID.randomUUID(), minutes?.let { minutes * 60000L } ?: 0L, false)
            stopWatchList.add(stopWatch)
            stopWatchListAdapter.submitList(stopWatchList)
            stopWatchListAdapter.notifyDataSetChanged()
        }

    }
}