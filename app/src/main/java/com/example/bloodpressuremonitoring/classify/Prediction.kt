package com.example.bloodpressuremonitoring.classify

import com.google.gson.annotations.SerializedName

class Prediction(msg: PredictionResult, result: Boolean, err: String) {
    @SerializedName("prediction")
    var msg:PredictionResult = msg
    @SerializedName("success")
    var result:Boolean = result
    @SerializedName("error_message")
    var err:String = err
}

class PredictionResult(dia: Double, sys: Double, pulse:Double) {
    @SerializedName("diastolic")
    var dia: Double = dia
    @SerializedName("systolic")
    var sys: Double = sys
    @SerializedName("heart_rate")
    var pulse: Double = pulse
}