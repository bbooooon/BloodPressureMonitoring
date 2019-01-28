package com.example.bloodpressuremonitoring.user

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.bloodpressuremonitoring.R
import com.example.bloodpressuremonitoring.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_signup.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class SignupAddressFragment : Fragment() {
    private var blood: String = ""
    private var address: String = ""
    lateinit var mAuth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.example.bloodpressuremonitoring.R.layout.fragment_signup_address, container, false)
        val signup_submit = view.findViewById<Button>(R.id.signup_submit_btn)

        Log.e("userinfo :  ", User.userinfo.toString())
        val addrtxt = view.findViewById<TextView>(R.id.address_input)
        val cityinput = view.findViewById<TextView>(R.id.city_input)
        val protxt = view.findViewById<TextView>(R.id.province_input)
        val countrytxt = view.findViewById<TextView>(R.id.country_input)
        val ziptxt = view.findViewById<TextView>(R.id.zip_input)
        val teltxt = view.findViewById<TextView>(R.id.tel_input)
        val dobtxt = view.findViewById<TextView>(R.id.dob_input)
        val allergytext = view.findViewById<TextView>(R.id.allergic_input)

        val typeSpinner = view.findViewById<Spinner>(R.id.blood_spinner)

        val bloodlist = resources.getStringArray(R.array.blood_list)
        val adapter = ArrayAdapter<String>(activity,
                android.R.layout.simple_dropdown_item_1line, bloodlist)
        typeSpinner.setAdapter(adapter)

        typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                blood = bloodlist[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) { }
        }

        val myCalendar = Calendar.getInstance()

//        val dt = Date()
//        val dateformat = SimpleDateFormat("dd/MM/yyyy")
//        dobtxt.text = dateformat.format(dt).toString()

        val startdate = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd/MM/yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            dobtxt.text = sdf.format(myCalendar.time)
        }

        dobtxt.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                DatePickerDialog(activity, startdate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show()
            }
        })

        signup_submit.setOnClickListener {
            val addr = addrtxt.text.toString()
            val city = cityinput.text.toString()
            val pro = protxt.text.toString()
            val coun = countrytxt.text.toString()
            val zip = ziptxt.text.toString()
            val tel = teltxt.text.toString()
            address = "$addr $city $pro $coun $zip"

            if (addr == "" || city == "" || pro == "" || coun == "") {
                Toast.makeText(context, "Input address", Toast.LENGTH_SHORT).show()
            }
            else if (zip == "") {
                Toast.makeText(context, "Input Zip code", Toast.LENGTH_SHORT).show()
            }
            else if (tel == "") {
                Toast.makeText(context, "Input telephone number", Toast.LENGTH_SHORT).show()
            }

            else if (blood == "หมู่โลหิต") {
                Toast.makeText(context, "Select blood type", Toast.LENGTH_SHORT).show()
            }

            else if (addr.isNotEmpty() && city.isNotEmpty() && pro.isNotEmpty() && coun.isNotEmpty() && zip.isNotEmpty()) {
//                if (dobtxt.text.toString() != confirm_pass) {
//                    Toast.makeText(context, "Incorrect password.", Toast.LENGTH_SHORT).show()
//                } else
                if (zip.length != 5) {
                    Toast.makeText(context, "zip code must contain 5 digits.", Toast.LENGTH_SHORT).show()
                } else if (tel.length != 10) {
                    Toast.makeText(context, "telephone number must contain 10 digits.", Toast.LENGTH_SHORT).show()
                } else {
                    User.userinfo.blood = blood
                    User.userinfo.address = address
                    val encrypted = AESCrypt.encrypt(User.userinfo.password)
                    User.userinfo.password = encrypted
                    User.userinfo.tel = tel
                    User.userinfo.dob = dobtxt.text.toString()
                    User.userinfo.admin = 0
                    User.userinfo.allergic = allergytext.text.toString()

                    saveData()
                }
            }
        }

        return view
    }

    private fun saveData() {
        //0 - male , 1 - female
//        val journalEntry1 = AddUser(email, name, id, encrypted.toString(),"","","","",0,"","$id.jpg",0)

        val dataReference = FirebaseDatabase.getInstance().getReference("User")
        val messageId = dataReference.push().key
        dataReference.child(messageId).setValue(User.userinfo).addOnCompleteListener {
            Toast.makeText(context, "Registration Success", Toast.LENGTH_SHORT).show()
        }

        val drawable = context.resources.getDrawable(R.drawable.user)
        val bitmap = (drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        val bt = baos.toByteArray()

        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference
        val imageRef = storageReference.child("user/${User.userinfo.hn}.jpg")
        imageRef.putBytes(bt)

        mAuth = FirebaseAuth.getInstance()
        mAuth.createUserWithEmailAndPassword(User.userinfo.email, User.userinfo.password)
                .addOnCompleteListener(this.activity) { task ->
                    if (!task.isSuccessful()) {
                        Toast.makeText(context, "Authentication failed." + task.exception, Toast.LENGTH_SHORT).show();
                    } else {
                        activity.finish()
                    }
                }
    }
}
