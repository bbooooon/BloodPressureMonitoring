package com.example.bloodpressuremonitoring

class MedicineInfo(val picture: String, val usage: String, val effect: String,
                    val medical_name: String, val trade_mark: String,
                   val manufacturer: String) {
    constructor() : this("", "", "","","","")
}