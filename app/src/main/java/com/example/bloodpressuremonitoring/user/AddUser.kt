package com.example.bloodpressuremonitoring.user

class AddUser (val email: String,val name: String, val hn: String, val password: String, val filename: String, val admin: Int) {
    constructor() : this("","",  "", "","", 0)
}
