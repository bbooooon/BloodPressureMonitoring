package com.example.bloodpressuremonitoring.user

class AddUser (val username: String, val hn: String, val password: String,
               val email: String) {
    constructor() : this("", "", "","")
}