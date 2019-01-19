package com.example.bloodpressuremonitoring.user

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.bloodpressuremonitoring.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_signup.*
import java.io.ByteArrayOutputStream

class SignupFragment : Fragment() {
    private var email: String = ""
    private var id: String = ""
    private var password: String = ""
    private var name: String = ""
    private var confirm_pass: String = ""
    private var email_list: MutableList<String> = mutableListOf()
    lateinit var  mAuth: FirebaseAuth
    lateinit var dataReference: DatabaseReference
    lateinit var msgList: MutableList<AddUser>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.example.bloodpressuremonitoring.R.layout.fragment_signup, container, false)
        val signup_submit = view.findViewById<Button>(R.id.signup_submit_btn)

        signup_submit.setOnClickListener{
            email = email_input.text.toString()
            id = id_input.text.toString()
            password = pass_input.text.toString()
            confirm_pass = conpass_input.text.toString()
            val rname = name_input.text.toString()
            val surname = surname_input.text.toString()
            name = rname+" "+surname

            if (id == "") {
                Toast.makeText(context, "Input HN number", Toast.LENGTH_SHORT).show()
            }
            if (email == "") {
                Toast.makeText(context, "Input email", Toast.LENGTH_SHORT).show()
            }
            if (password == "") {
                Toast.makeText(context, "Input password", Toast.LENGTH_SHORT).show()
            }
            if (confirm_pass == "") {
                Toast.makeText(context, "Input password", Toast.LENGTH_SHORT).show()
            }
            if (email.isNotEmpty() && password.isNotEmpty() && confirm_pass.isNotEmpty()) {
                if (email in email_list) {
                    Toast.makeText(context, "Please change email.", Toast.LENGTH_SHORT).show()
                }
                else if (password != confirm_pass) {
                    Toast.makeText(context, "Incorrect password.", Toast.LENGTH_SHORT).show()
                }
                else if (password.length < 6) {
                    Toast.makeText(context, "Password is too short.", Toast.LENGTH_SHORT).show()
                }
                else if (id.length < 8) {
                    Toast.makeText(context, "HN number must contain 8 digits.", Toast.LENGTH_SHORT).show()
                }
                else {
                    saveData()
//                    val intent = Intent(this.context, MainActivity::class.java)
//                    startActivity(intent)
                }
            }
        }

        dataReference = FirebaseDatabase.getInstance().getReference("User")
        msgList = mutableListOf()
        dataReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(p0: DataSnapshot?) {
                if (p0!!.exists()) {
                    msgList.clear()
                    for (i in p0.children) {
                        val message = i.getValue(AddUser::class.java)
                        msgList.add(message!!)
                        email_list.add(message.email)
                    }
                }
            }
        })

        return view
    }

    private fun saveData() {
        val messageId = dataReference.push().key
        val journalEntry1 = AddUser(email, name, id, password,"$id.jpg",0)
        dataReference.child(messageId).setValue(journalEntry1).addOnCompleteListener {
            Toast.makeText(context, "Registration Success", Toast.LENGTH_SHORT).show()
        }

        val drawable = context.resources.getDrawable(R.drawable.user)
        val bitmap = (drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val bt = baos.toByteArray()

        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference
        val imageRef = storageReference.child("user/$id.jpg")
        val uploadTask = imageRef.putBytes(bt)

        mAuth = FirebaseAuth.getInstance()
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this.activity) { task ->
                    if (!task.isSuccessful()) {
                        Toast.makeText(context, "Authentication failed." + task.getException(),Toast.LENGTH_SHORT).show();
                    } else {
                        activity.finish()
                        val intent = Intent(this.context, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
    }
}
