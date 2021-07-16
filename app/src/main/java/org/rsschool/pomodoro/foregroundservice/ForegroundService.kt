package org.rsschool.pomodoro.foregroundservice

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import org.rsschool.pomodoro.R
import org.rsschool.pomodoro.extension.*
import org.rsschool.pomodoro.model.TimerWatch
import org.rsschool.pomodoro.ui.MainActivity
import java.lang.reflect.Type

class ForegroundService : Service() {

    private var isServiceStarted = false
    private var notificationManager: NotificationManager? = null
    private var job: Job? = null

    private val builder by lazy {
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Pomodoro")
            .setGroup("Pomodoro")
            .setGroupSummary(false)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(getPendingIntent())
            .setSilent(true)
            .setSmallIcon(R.drawable.ic_baseline_access_alarm_24)
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        processCommand(intent)
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun processCommand(intent: Intent?) {
        when (intent?.extras?.getString(COMMAND_ID) ?: INVALID) {
            COMMAND_START -> {
                val startTime = intent?.extras?.getLong(STARTED_TIMER_TIME_MS) ?: return
                commandStart(startTime)
            }
            COMMAND_STOP -> commandStop()
            INVALID -> return
        }
    }

    private fun commandStart(startTime: Long) {
        if (isServiceStarted) {
            return
        }
        try {
            moveToStartedState()
            startForegroundAndShowNotification()
            continueTimer(startTime)
        } finally {
            isServiceStarted = true
        }
    }

    private fun continueTimer(untilFinishedTime: Long) {
        job = GlobalScope.launch(Dispatchers.Main) {
            var untilFinish = untilFinishedTime - System.currentTimeMillis()
            while (untilFinish > 0) {
                Log.d("SERVICE", untilFinish.displayTime())
                notificationManager?.notify(
                    NOTIFICATION_ID,
                    getNotification(untilFinish.displayTime())
                )
                updateTimeInStorage(untilFinish)
                delay(INTERVAL)
                untilFinish = untilFinishedTime - System.currentTimeMillis()
            }
            timeOverNotify()
        }
    }

    private fun timeOverNotify() {
        try {
            val notify: Uri =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(
                baseContext,
                notify
            )
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val v: Vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }
    }

    private fun updateTimeInStorage(untilFinish: Long) {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences(STORE_FILE_NAME, MODE_PRIVATE)
        val serializedObject: String? =
            sharedPreferences.getString(TIMER_LIST, null)
        if (serializedObject != null) {
            val gson = Gson()
            val type: Type = object : TypeToken<List<TimerWatch?>?>() {}.type
            val stopWatchList: MutableList<TimerWatch> =
                gson.fromJson(serializedObject, type)
            stopWatchList.find { it.isStarted }?.untilFinishedMs = untilFinish
            val editor = sharedPreferences.edit()
            val json = gson.toJson(stopWatchList)
            editor.clear()
            editor.putString(TIMER_LIST, json)
            editor.apply()
        }
    }

    private fun commandStop() {
        if (!isServiceStarted) {
            return
        }
        try {
            job?.cancel()
            stopForeground(true)
            stopSelf()
        } finally {
            isServiceStarted = false
        }
    }

    private fun moveToStartedState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, ForegroundService::class.java))
        } else {
            startService(Intent(this, ForegroundService::class.java))
        }
    }

    private fun startForegroundAndShowNotification() {
        createChannel()
        val notification = getNotification("content")
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun getNotification(content: String) = builder.setContentText(content).build()


    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "pomodoro"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(
                CHANNEL_ID, channelName, importance
            )
            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }

    private fun getPendingIntent(): PendingIntent? {
        val resultIntent = Intent(this, MainActivity::class.java)
        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        return PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT)
    }

    private companion object {
        private const val CHANNEL_ID = "channelId"
        private const val NOTIFICATION_ID = 777
        private const val INTERVAL = 1000L
    }


}