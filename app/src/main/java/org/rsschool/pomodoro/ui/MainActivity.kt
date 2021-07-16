package org.rsschool.pomodoro.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.rsschool.pomodoro.databinding.ActivityMainBinding
import org.rsschool.pomodoro.extension.*
import org.rsschool.pomodoro.foregroundservice.ForegroundService
import org.rsschool.pomodoro.model.TimerWatch
import org.rsschool.pomodoro.ui.adapters.StopWatchListAdapter
import java.lang.reflect.Type
import java.util.*


class MainActivity : AppCompatActivity(), StopWatchListener {

    private lateinit var binding: ActivityMainBinding
    private val stopWatchListAdapter = StopWatchListAdapter(this)
    private var stopWatchList = mutableListOf<TimerWatch>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.timersList.adapter = stopWatchListAdapter

        binding.addTimerButton.setOnClickListener {
            if (binding.textEditMinutes.text.isNullOrEmpty() || binding.textEditMinutes.text?.get(0) == '0') {
                binding.textEditMinutes.error = "can't be empty"
            } else {
                val minutes = binding.textEditMinutes.text.toString().toLongOrNull()
                val stopWatch =
                    TimerWatch(UUID.randomUUID(), minutes?.let { minutes * 60000L } ?: 0L, false)
                stopWatchList.add(stopWatch)
                stopWatchListAdapter.submitList(stopWatchList)
                stopWatchListAdapter.notifyItemInserted(stopWatchList.size - 1)
            }
        }
    }

    private fun fetchTimerList() {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences(STORE_FILE_NAME, Context.MODE_PRIVATE)
        val serializedObject: String? = sharedPreferences.getString(TIMER_LIST, null)
        if (serializedObject != null) {
            val gson = Gson()
            val type: Type = object : TypeToken<List<TimerWatch?>?>() {}.type
            stopWatchList = gson.fromJson(serializedObject, type)
            stopWatchListAdapter.submitList(stopWatchList)
            stopWatchListAdapter.notifyDataSetChanged()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("Pause", "onPAUSE")
    }


    override fun onStop() {
        super.onStop()
        Log.d("ON_STOP", "ON_STOP")
        val startIntent = Intent(this, ForegroundService::class.java)
        startIntent.putExtra(COMMAND_ID, COMMAND_START)
        val timerWatch = stopWatchList.find { it.isStarted }
        if (timerWatch != null) {
            timerWatch.countDownTimer?.cancel()
            startIntent.putExtra(
                STARTED_TIMER_TIME_MS,
                System.currentTimeMillis() + timerWatch.untilFinishedMs
            )
            startService(startIntent)
        }
        saveTimeWatchList()
    }

    private fun saveTimeWatchList() {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences(STORE_FILE_NAME, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(stopWatchList)
        editor.clear()
        editor.putString(TIMER_LIST, json)
        editor.apply()
    }

    override fun onStart() {
        super.onStart()
        Log.d("ON_START", "ON_START")
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
        fetchTimerList()
    }


    override fun delete(id: UUID, position: Int) {
        stopWatchList.remove(stopWatchList.find { it.id == id })
        stopWatchListAdapter.notifyItemRemoved(position)
        stopWatchListAdapter.notifyItemRangeChanged(position, stopWatchList.size)
    }

    override fun start(timerWatch: TimerWatch, position: Int) {
        changeTimerWatch(timerWatch = timerWatch, isStarted = true, position = position)
    }

    override fun stop(timerWatch: TimerWatch, position: Int) {
        changeTimerWatch(timerWatch = timerWatch, isStarted = false, position = position)
    }

    override fun setTimer(timer: CountDownTimer, id: UUID) {
        stopWatchList.find { it.id == id }?.countDownTimer = timer
    }

    private fun changeTimerWatch(timerWatch: TimerWatch, isStarted: Boolean, position: Int) {
        stopWatchList.find { it.id == timerWatch.id }?.isStarted = isStarted
        stopWatchListAdapter.notifyItemChanged(position)
    }
}