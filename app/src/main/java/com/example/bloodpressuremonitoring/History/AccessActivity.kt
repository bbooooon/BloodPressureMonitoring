package com.example.bloodpressuremonitoring.History

import android.annotation.TargetApi
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.support.annotation.RequiresApi
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.bloodpressuremonitoring.BPObject
import com.example.bloodpressuremonitoring.HomeActivity
import com.example.bloodpressuremonitoring.R
import com.example.bloodpressuremonitoring.SessionManager
import com.example.bloodpressuremonitoring.classify.UserData
import com.example.bloodpressuremonitoring.user.AESCrypt
import com.example.bloodpressuremonitoring.user.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class AccessActivity : AppCompatActivity() {
    lateinit var session: SessionManager
    private lateinit var timer: CountDownTimer
    private var timerLengthSeconds: Long = 0
    private var timerState = TimerState.Stopped
    private var secondsRemaining: Long = 0
    lateinit var loadingDialog: ProgressDialog
    lateinit var mAuth: FirebaseAuth

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

        val toolbar = findViewById<Toolbar>(R.id.access_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val password_txt = findViewById<EditText>(R.id.password)
        val buttonCancel = findViewById<Button>(R.id.button_cancel)
        val buttonLogin = findViewById<Button>(R.id.button_login)

        session = SessionManager(applicationContext)

        val user = session.userDetails
        val hn = user.get(SessionManager.KEY_HN)
        val email = user.get(SessionManager.KEY_EMAIL)
        val aname = user.get(SessionManager.KEY_NAME)

        buttonCancel.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                finish()
            }
        })

        buttonLogin.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val pass = password_txt.text.toString()
                mAuth = FirebaseAuth.getInstance()
                mAuth.signInWithEmailAndPassword(email!!, pass).addOnCompleteListener(this@AccessActivity) { task ->
                    if (!task.isSuccessful) {
                        Log.w(ContentValues.TAG, "signInWithEmail", task.getException());
                        Toast.makeText(this@AccessActivity, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        startTimer()
                        timerState =  AccessActivity.TimerState.Running

                        loadingDialog = ProgressDialog.show(this@AccessActivity, "", "loading...", true, false)
                        val dataReference = FirebaseDatabase.getInstance().getReference("UserData").child(hn)
                        dataReference.addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError?) {
                            }
                            override fun onDataChange(p0: DataSnapshot?) {
                                val msgList = arrayListOf<UserData>()
                                if (p0!!.exists()) {
                                    msgList.clear()
                                    BPObject.bplist.clear()
                                    BPObject.timelist.clear()
                                    for (i in p0.children) {
                                        val message = i.getValue(UserData::class.java)
                                        msgList.add(message!!)
                                        BPObject.bplist = msgList

                                        val date = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").parse(message!!.runtimestamp)
                                        val dateformat = SimpleDateFormat("dd/MM/yyyy HH:mm").format(date)
                                        Log.e("date format", dateformat)
                                        BPObject.timelist.add(dateformat)
                                    }
                                }
                                loadingDialog.dismiss()
                                User.access = true
                                finish()
                                val intent = Intent(this@AccessActivity, HistoryActivity::class.java)
                                intent.putExtra("hn", hn)
                                intent.putExtra("email", email)
                                intent.putExtra("name", aname)
                                startActivity(intent)
                            }
                        })
                    }
                }

//                if (!encrypted.equals(re)) {
//                    Toast.makeText(applicationContext, "Failed!",
//                            Toast.LENGTH_SHORT).show()
//                } else {
//                    startTimer()
//                    timerState =  AccessActivity.TimerState.Running
//
//                    loadingDialog = ProgressDialog.show(this@AccessActivity, "", "loading...", true, false)
//                    val dataReference = FirebaseDatabase.getInstance().getReference("UserData").child(hn)
//                    dataReference.addValueEventListener(object : ValueEventListener {
//                        override fun onCancelled(p0: DatabaseError?) {
//                        }
//                        override fun onDataChange(p0: DataSnapshot?) {
//                            val msgList = arrayListOf<UserData>()
//                            if (p0!!.exists()) {
//                                msgList.clear()
//                                BPObject.bplist.clear()
//                                BPObject.timelist.clear()
//                                for (i in p0.children) {
//                                    val message = i.getValue(UserData::class.java)
//                                    msgList.add(message!!)
//                                    BPObject.bplist = msgList
//
//                                    val date = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").parse(message!!.runtimestamp)
//                                    val dateformat = SimpleDateFormat("dd/MM/yyyy HH:mm").format(date)
//                                    Log.e("date format", dateformat)
//                                    BPObject.timelist.add(dateformat)
//                                }
//                            }
//                            loadingDialog.dismiss()
//                            User.access = true
//                            finish()
//                            val intent = Intent(this@AccessActivity, HistoryActivity::class.java)
//                            intent.putExtra("hn", hn)
//                            intent.putExtra("email", email)
//                            intent.putExtra("name", aname)
//                            startActivity(intent)
//                        }
//                    })
//                }
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
        }
        else if (timerState == AccessActivity.TimerState.Running)
            startTimer()
    }

    private fun onTimerFinished(){
        User.access = false
        timerState = AccessActivity.TimerState.Stopped
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
}
