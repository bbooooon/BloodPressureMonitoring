package com.example.bloodpressuremonitoring.History

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        PrefUtil.setTimerState(AccessActivity.TimerState.Stopped, context)
        PrefUtil.setAlarmSetTime(0, context)
    }
}
