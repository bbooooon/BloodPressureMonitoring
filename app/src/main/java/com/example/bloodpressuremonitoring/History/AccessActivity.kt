package com.example.bloodpressuremonitoring.History

import android.annotation.TargetApi
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.support.annotation.RequiresApi
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.bloodpressuremonitoring.R
import com.example.bloodpressuremonitoring.SessionManager
import com.example.bloodpressuremonitoring.user.AddUser
import com.example.bloodpressuremonitoring.user.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class AccessActivity : AppCompatActivity() {
    lateinit var session: SessionManager
    private lateinit var timer: CountDownTimer
    private var timerLengthSeconds: Long = 0
    private var timerState = TimerState.Stopped
    private var secondsRemaining: Long = 0

    companion object {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @RequiresApi(Build.VERSION_CODES.KITKAT)
        fun setAlarm(context: Context, nowSeconds: Long, secondsRemaining: Long): Long{
            val wakeUpTime = (nowSeconds + secondsRemaining) * 1000
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)
            PrefUtil.setAlarmSetTime(nowSeconds, context)
            return wakeUpTime
        }

        fun removeAlarm(context: Context){
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            PrefUtil.setAlarmSetTime(0, context)
        }

        val nowSeconds: Long
            get() = Calendar.getInstance().timeInMillis / 1000
    }

    enum class TimerState{
        Stopped, Paused, Running
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_access)

        val password = findViewById<EditText>(R.id.password)
        val buttonCancel = findViewById(R.id.button_cancel) as Button
        val buttonLogin = findViewById(R.id.button_login) as Button

        session = SessionManager(applicationContext)

        val user = session.userDetails
        val hn = user.get(SessionManager.KEY_HN)

        var addUser: AddUser = AddUser()
        val dataReference = FirebaseDatabase.getInstance().getReference("User")
        dataReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(p0: DataSnapshot?) {
                if (p0!!.exists()) {
                    addUser = p0.getValue(AddUser::class.java)!!
                }
            }
        })

        buttonCancel.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                finish()
            }
        })

        buttonLogin.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                if (password.text.equals(addUser.password)) {
                    startTimer()
                    timerState =  AccessActivity.TimerState.Running

                    User.access = true
                    val intent = Intent(this@AccessActivity, HistoryActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(applicationContext, "Failed!",
                            Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        initTimer()
        AccessActivity.removeAlarm(this)
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onPause() {
        super.onPause()

        if (timerState == AccessActivity.TimerState.Running){
            timer.cancel()
            val wakeUpTime = AccessActivity.setAlarm(this, AccessActivity.nowSeconds, secondsRemaining)
        }
        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds, this)
        PrefUtil.setSecondsRemaining(secondsRemaining, this)
        PrefUtil.setTimerState(timerState, this)
    }

    private fun initTimer(){
        timerState = PrefUtil.getTimerState(this)

        if (timerState == AccessActivity.TimerState.Stopped)
            setNewTimerLength()
        else
            setPreviousTimerLength()

        secondsRemaining = if (timerState == AccessActivity.TimerState.Running || timerState == AccessActivity.TimerState.Paused)
            PrefUtil.getSecondsRemaining(this)
        else
            timerLengthSeconds

        val alarmSetTime = PrefUtil.getAlarmSetTime(this)
        if (alarmSetTime > 0)
            secondsRemaining -= AccessActivity.nowSeconds - alarmSetTime

        if (secondsRemaining <= 0) {
            onTimerFinished()
            User.access = false
        }
        else if (timerState == AccessActivity.TimerState.Running)
            startTimer()
    }

    private fun onTimerFinished(){
        timerState = AccessActivity.TimerState.Stopped

        //set the length of the timer to be the one set in SettingsActivity
        //if the length was changed when the timer was running
        setNewTimerLength()

        PrefUtil.setSecondsRemaining(timerLengthSeconds, this)
        secondsRemaining = timerLengthSeconds
    }

    private fun startTimer(){
        timerState = AccessActivity.TimerState.Running

        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
            }
        }.start()
    }

    private fun setNewTimerLength(){
        val lengthInMinutes = PrefUtil.getTimerLength(this)
        timerLengthSeconds = (lengthInMinutes * 60L)
    }

    private fun setPreviousTimerLength(){
        timerLengthSeconds = PrefUtil.getPreviousTimerLengthSeconds(this)
    }

    private fun updateCountdownUI(){
        val minutesUntilFinished = secondsRemaining / 60
        val secondsInMinuteUntilFinished = secondsRemaining - minutesUntilFinished * 60
        val secondsStr = secondsInMinuteUntilFinished.toString()
//        timer_text.text = "$minutesUntilFinished:${if (secondsStr.length == 2) secondsStr else "0" + secondsStr}"
    }
}
