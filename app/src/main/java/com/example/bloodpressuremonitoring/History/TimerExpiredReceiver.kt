package com.example.bloodpressuremonitoring.History

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.bloodpressuremonitoring.user.User

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        User.access = false
        PrefUtil.setTimerState(AccessActivity.TimerState.Stopped, context)
        PrefUtil.setAlarmSetTime(0, context)
    }
}
