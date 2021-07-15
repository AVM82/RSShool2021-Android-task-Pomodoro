package org.rsschool.pomodoro

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import org.rsschool.pomodoro.ui.MainActivity


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(MainActivity())
    }
}