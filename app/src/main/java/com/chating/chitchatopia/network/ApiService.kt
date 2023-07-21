package com.chating.chitchatopia.network

import android.telecom.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface ApiService {

    @POST("send")
    fun sendRemoteMessage(
        @HeaderMap headers:HashMap<String,String>,
        @Body remoteBody:String
    ):retrofit2.Call<String>
}