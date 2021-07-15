package org.rsschool.pomodoro.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.rsschool.pomodoro.databinding.ActivityMainBinding
import org.rsschool.pomodoro.extension.COMMAND_ID
import org.rsschool.pomodoro.extension.COMMAND_START
import org.rsschool.pomodoro.extension.COMMAND_STOP
import org.rsschool.pomodoro.extension.STARTED_TIMER_TIME_MS
import org.rsschool.pomodoro.foregroundservice.ForegroundService
import org.rsschool.pomodoro.model.TimerWatch
import org.rsschool.pomodoro.storage.TimerRepository.Companion.STORE_FILE_NAME
import org.rsschool.pomodoro.storage.TimerRepository.Companion.TIMER_LIST
import org.rsschool.pomodoro.ui.adapters.StopWatchListAdapter
import java.lang.reflect.Type
import java.util.*


class MainActivity : AppCompatActivity(), StopWatchListener, LifecycleObserver {

//    private val mContext: Context? = getContext()
//
//    fun getContext(): Context? {
//        return mContext
//    }

    private lateinit var binding: ActivityMainBinding
    private val stopWatchListAdapter = StopWatchListAdapter(this)
    private var stopWatchList = mutableListOf<TimerWatch>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.timersList.adapter = stopWatchListAdapter
        fetchTimerList()

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

    override fun onStop() {
        super.onStop()
        val sharedPreferences: SharedPreferences =
            getSharedPreferences(STORE_FILE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(stopWatchList)
        editor.clear()
        editor.putString(TIMER_LIST, json);
        editor.apply();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        val startIntent = Intent(this, ForegroundService::class.java)
        startIntent.putExtra(COMMAND_ID, COMMAND_START)
        stopWatchList.find { it.isStarted }?.countDownTimer?.cancel()
        startIntent.putExtra(STARTED_TIMER_TIME_MS, System.currentTimeMillis())
        startService(startIntent)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
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

    private fun changeTimerWatch(timerWatch: TimerWatch, isStarted: Boolean, position: Int) {
        stopWatchList.find { it.id == timerWatch.id }?.isStarted = isStarted
        stopWatchListAdapter.notifyItemChanged(position)
    }
}