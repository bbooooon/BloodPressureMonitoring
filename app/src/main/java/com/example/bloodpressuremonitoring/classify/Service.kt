package com.example.bloodpressuremonitoring.classify

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Multipart
import retrofit2.http.Part

/**
 * Created by Bajarng on 22/1/2561.
 */
interface Service{
    @Multipart
    @POST("/predict")
    fun postImage(@Part file: MultipartBody.Part, @Part hn: MultipartBody.Part, @Part username: MultipartBody.Part, @Part timestamp: MultipartBody.Part): Call<Prediction>
}