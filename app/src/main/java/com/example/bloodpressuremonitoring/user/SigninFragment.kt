package com.example.bloodpressuremonitoring.user

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.bloodpressuremonitoring.R
import com.example.bloodpressuremonitoring.SessionManager
import com.example.bloodpressuremonitoring.classify.CameraActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_signin.*

class SigninFragment : Fragment() {
    private var username_list: MutableList<String> = mutableListOf()
    private var password_list: MutableList<String> = mutableListOf()
    private var hn_list: MutableList<String> = mutableListOf()
    lateinit var  mAuth: FirebaseAuth
    lateinit var dataReference: DatabaseReference
    lateinit var msgList: MutableList<AddUser>
    lateinit var session: SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.example.bloodpressuremonitoring.R.layout.fragment_signin, container, false)
        val signin_submit = view.findViewById<Button>(R.id.signin_submit_btn)

        dataReference = FirebaseDatabase.getInstance().getReference("User")
        msgList = mutableListOf()
        dataReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(p0: DataSnapshot?) {
                if (p0!!.exists()) {
                    msgList.clear()
                    username_list.clear()
                    password_list.clear()
                    for (i in p0.children) {
                        val message = i.getValue(AddUser::class.java)
                        msgList.add(message!!)
                        username_list.add(message.username)
                        password_list.add(message.password)
                        hn_list.add(message.hn)
                    }
                }
            }
        })

        signin_submit.setOnClickListener {
            val username: String = signin_user_input.text.toString()
            val password: String = signin_pass_input.text.toString()
            val userindex = username_list.indexOf(username)
            for ((index, value) in username_list.withIndex()) {
                if (username in username_list[index] && password.equals(password_list[index])) {
                    activity.finish()

                    val user: String = username_list[userindex]
                    val hn = hn_list[index]
                    Log.e("password ------>  ",password_list[userindex] )
                    Log.e("Username ------>  ",username_list[userindex] )
//                    mAuth = FirebaseAuth.getInstance()
//                    mAuth.signInWithEmailAndPassword(email_list[userindex], password).addOnCompleteListener(this.activity, OnCompleteListener<AuthResult> { task ->
//                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "signInWithEmail", task.getException());
//                            Toast.makeText(this.context, "Authentication failed.", Toast.LENGTH_SHORT).show();
//                        }
//                        else {
//                            val intent = Intent(context, CameraActivity::class.java)
////                            intent.putExtra("id", mAuth.currentUser?.email)
//                            startActivity(intent)
//                        }
//                    })
                    session = SessionManager(context)
                    session.createLoginSession(user, hn)

                    val intent = Intent(context, CameraActivity::class.java)
                    User.getUser().username = username_list[index]
                    startActivity(intent)
                }
                if (username in username_list[index] && password != password_list[index]) {
                    Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show()
                }
            }
            if (username !in username_list || password !in password_list) {
                Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show()
            }
            if (username in username_list && password !in password_list) {
                Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }


}
