package com.example.bloodpressuremonitoring.user

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.bloodpressuremonitoring.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_signup.*

class SignupFragment : Fragment() {
    private var email: String = ""
    private var id: String = ""
    private var password: String = ""
    private var name: String = ""
    private var confirm_pass: String = ""
    private var gender: Int = 0
    private var email_list: MutableList<String> = mutableListOf()
    private var hn_list: MutableList<String> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.example.bloodpressuremonitoring.R.layout.fragment_signup, container, false)
        val signup_next = view.findViewById<Button>(R.id.signup_next_btn)

        val emailtxt = view.findViewById<TextView>(R.id.email_input)
        val idinput = view.findViewById<TextView>(R.id.id_input)
        val passtxt = view.findViewById<TextView>(R.id.pass_input)
        val conpasstxt = view.findViewById<TextView>(R.id.conpass_input)
        val nametxt = view.findViewById<TextView>(R.id.name_input)
        val surnametxt = view.findViewById<TextView>(R.id.surname_input)

        val typeSpinner = view.findViewById<Spinner>(R.id.gender_spinner)

        val genderlist = resources.getStringArray(R.array.gender_list)
        val adapter = ArrayAdapter<String>(activity,
                android.R.layout.simple_dropdown_item_1line, genderlist)
        typeSpinner.setAdapter(adapter)

        typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                gender = position
            }
            override fun onNothingSelected(parent: AdapterView<*>) { }
        }

        val dataReference = FirebaseDatabase.getInstance().getReference("User")
        val msgList : MutableList<AddUser> = mutableListOf()
        dataReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(p0: DataSnapshot?) {
                email_list.clear()
                hn_list.clear()
                if (p0!!.exists()) {
                    msgList.clear()
                    for (i in p0.children) {
                        val message = i.getValue(AddUser::class.java)
                        msgList.add(message!!)
                        email_list.add(message.email)
                        hn_list.add(message.hn)
                    }
                }
            }
        })

        signup_next.setOnClickListener {
            email = emailtxt.text.toString()
            id = idinput.text.toString()
            password = passtxt.text.toString()
            confirm_pass = conpasstxt.text.toString()
            val rname = nametxt.text.toString()
            val surname = surnametxt.text.toString()
            name = "$rname $surname"

            if (rname == "" || surname == "") {
                Toast.makeText(context, "Input name", Toast.LENGTH_SHORT).show()
            }
            else if (id == "") {
                Toast.makeText(context, "Input HN number", Toast.LENGTH_SHORT).show()
            }
            else if (email == "") {
                Toast.makeText(context, "Input email", Toast.LENGTH_SHORT).show()
            }
            else if (password == "") {
                Toast.makeText(context, "Input password", Toast.LENGTH_SHORT).show()
            }
            else if (confirm_pass == "") {
                Toast.makeText(context, "Input password", Toast.LENGTH_SHORT).show()
            }
            else if (gender == 0) {
                Toast.makeText(context, "Select gender", Toast.LENGTH_SHORT).show()
            }
            else if (email.isNotEmpty() && password.isNotEmpty() && confirm_pass.isNotEmpty() && rname.isNotEmpty() && surname.isNotEmpty()) {
                if (email in email_list) {
                    Toast.makeText(context, "Please change email.", Toast.LENGTH_SHORT).show()
                } else if (id in hn_list) {
                    Toast.makeText(context, "Please change hn number.", Toast.LENGTH_SHORT).show()
                }else if (password != confirm_pass) {
                    Toast.makeText(context, "Incorrect password.", Toast.LENGTH_SHORT).show()
                } else if (password.length < 6) {
                    Toast.makeText(context, "Password is too short.", Toast.LENGTH_SHORT).show()
                } else if (id.length < 8) {
                    Toast.makeText(context, "HN number must contain 8 digits.", Toast.LENGTH_SHORT).show()
                } else {
                    User.userinfo.email = email
                    User.userinfo.hn = id
                    User.userinfo.filename = "$id.jpg"
                    User.userinfo.password = password
                    User.userinfo.name = name
                    User.userinfo.gender = gender-1

                    val fragment1 = SignupAddressFragment()
                    val fragmentTransaction1 = activity.supportFragmentManager.beginTransaction()
                    fragmentTransaction1.replace(R.id.fragment, fragment1)
                    fragmentTransaction1.commit()

                }
            }
        }
        return view
    }
}
