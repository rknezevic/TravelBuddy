package com.robertknezevic.travelbuddy.data.api

import com.robertknezevic.travelbuddy.data.model.CitiesResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

const val BASE_URL = "https://wft-geo-db.p.rapidapi.com/v1/"
const val RAPIDAPI_KEY = "d72cce1e07mshdb16742cf762508p139ceejsn9048312565f3"
const val RAPIDAPI_HOST = "wft-geo-db.p.rapidapi.com"

interface RetrofitService {
    @Headers(
        "X-RapidAPI-Key: $RAPIDAPI_KEY",
        "X-RapidAPI-Host: $RAPIDAPI_HOST"
    )
    @GET("geo/cities")
    suspend fun getCities(
        @Query("namePrefix") namePrefix: String,
        @Query("minPopulation") minPopulation: Int = 1000,
        @Query("limit") limit: Int = 10
    ): CitiesResponse

    companion object{
        fun create() : RetrofitService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(RetrofitService::class.java)
        }
    }


}