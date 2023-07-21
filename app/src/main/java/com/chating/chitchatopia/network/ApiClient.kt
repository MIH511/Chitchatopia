package com.chating.chitchatopia.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
 object ApiClient {

        private var retrofit: Retrofit? = null

        fun retrofit(): Retrofit {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl("https://fcm.googleapis.com/fcm/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build()
            }
            return retrofit!!
        }
 }

