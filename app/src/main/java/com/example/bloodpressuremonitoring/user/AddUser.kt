package com.example.bloodpressuremonitoring.user

class AddUser (var email: String,var name: String, var hn: String, var password: String,
               var tel: String, var address: String, var dob: String, var allergic: String,
               var gender: Int, var blood: String, var filename: String, var admin: Int) {
    constructor() : this("","",  "", "","","","",
            "",0,"", "", 0)
}
