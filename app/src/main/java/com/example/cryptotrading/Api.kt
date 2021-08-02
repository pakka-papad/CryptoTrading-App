package com.example.cryptotrading

import com.example.cryptotrading.models.PriceData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface Api {
    @Headers("X-CoinAPI-Key:DF2C0953-F3B2-45D4-95EE-CCF3B2BB16BF")
    @GET("{asset_id_base}/{asset_id_quote}/history")
    fun getRangeData(
        @Path("asset_id_base") asset_id_base: String,
        @Path("asset_id_quote") asset_id_quote: String,
        @QueryMap options: Map<String, String>
    ): Call<ArrayList<PriceData>>

}