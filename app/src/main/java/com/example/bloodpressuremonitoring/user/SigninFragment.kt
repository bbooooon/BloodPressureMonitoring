package com.example.bloodpressuremonitoring.user

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.bloodpressuremonitoring.HomeActivity
import com.example.bloodpressuremonitoring.R
import com.example.bloodpressuremonitoring.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_signin.*

class SigninFragment : Fragment() {
    private var email_list: MutableList<String> = mutableListOf()
    private var name_list: MutableList<String> = mutableListOf()
    private var password_list: MutableList<String> = mutableListOf()
    private var admin_list: MutableList<Int> = mutableListOf()
    private var hn_list: MutableList<String> = mutableListOf()
    lateinit var mAuth: FirebaseAuth
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
                    email_list.clear()
                    password_list.clear()
                    for (i in p0.children) {
                        val message = i.getValue(AddUser::class.java)
                        msgList.add(message!!)
                        email_list.add(message.email)
                        password_list.add(message.password)
                        name_list.add(message.name)
                        hn_list.add(message.hn)
                        admin_list.add(message.admin)
                    }
                }
            }
        })

        signin_submit.setOnClickListener {
            val email: String = signin_email_input.text.toString()
            val password: String = signin_pass_input.text.toString()

            val userindex = email_list.indexOf(email)
            for ((index, value) in email_list.withIndex()) {
                if (email in email_list[index] && password.equals(password_list[index])) {
                    activity.finish()

                    val em: String = email_list[userindex]
                    val n: String = name_list[userindex]
                    val hn = hn_list[index]

                    session = SessionManager(context)
                    session.createLoginSession(n, em, hn)

                    mAuth = FirebaseAuth.getInstance()
                    mAuth.signInWithEmailAndPassword(email_list[userindex], password).addOnCompleteListener(this.activity) { task ->
                        if (!task.isSuccessful) {
                            Log.w(TAG, "signInWithEmail", task.getException());
                            Toast.makeText(this.context, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    User.getUser().email = email_list[index]
                    val intent = Intent(context, HomeActivity::class.java)
                    startActivity(intent)
//                    val intent = Intent(context, HomeActivity::class.java)
//                    startActivity(intent)
                }
                if (email in email_list[index] && password != password_list[index]) {
                    Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show()
                }
            }
            if (email !in email_list || password !in password_list) {
                Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show()
            }
            if (email in email_list && password !in password_list) {
                Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }


}
