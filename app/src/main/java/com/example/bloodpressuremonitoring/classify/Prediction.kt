package com.example.bloodpressuremonitoring.classify

import com.google.gson.annotations.SerializedName

class Prediction(msg: PredictionResult, result: Int) {
    @SerializedName("prediction")
    var msg:PredictionResult = msg
    @SerializedName("result")
    var result:Int = result
}

class PredictionResult(dia: Int, sys: Int, pulse:Int) {
    @SerializedName("diastolic")
    var dia: Int = dia
    @SerializedName("systolic")
    var sys: Int = sys
    @SerializedName("heart_rate")
    var pulse: Int = pulse
}