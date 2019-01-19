package com.example.bloodpressuremonitoring.user
import com.example.bloodpressuremonitoring.classify.UserData

class AllUser (val nameList: ArrayList<String>, val userdataList: ArrayList<UserData>) {
    constructor() : this(arrayListOf(), arrayListOf())
}