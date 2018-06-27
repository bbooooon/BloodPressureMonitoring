package com.example.bloodpressuremonitoring.classify

import android.gesture.Prediction
import retrofit2.Call
import retrofit2.http.GET

public interface Api {
    @GET("run")
    fun getPrediction(): Call<Prediction>
}