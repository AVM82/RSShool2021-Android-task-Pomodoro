package org.rsschool.pomodoro.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.rsschool.pomodoro.databinding.ActivityMainBinding
import org.rsschool.pomodoro.ui.adapters.StopWatchListAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.timersList.adapter = StopWatchListAdapter()
    }
}