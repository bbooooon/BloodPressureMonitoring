package com.example.bloodpressuremonitoring.classify

class UserData(val dia: String, val sys: String, val pulse: String, val location: String, val posture: String, val arm: String, val phototimestamp : String, val runtimestamp: String) {
//    var dia: String = dia
//    var sys: String = sys
//    var pulse: String = pulse
//    var location: String = location
//    var posture: String = posture
    constructor() : this("","","","","","","","")
}