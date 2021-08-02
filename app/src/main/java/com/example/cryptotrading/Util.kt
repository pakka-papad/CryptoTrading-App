package com.example.cryptotrading

import android.annotation.SuppressLint
import com.example.cryptotrading.models.DayStats
import com.example.cryptotrading.models.PriceData
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Util {
    fun getDateStringed(year: Int, month: Int, date: Int): String{
        var out = year.toString()
        val addedOne = month + 1
        out += if(month<10){
            "-0$addedOne"
        } else{
            "-$addedOne"
        }
        out += if(date<10){
            "-0$date"
        } else{
            "-$date"
        }
        return out
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateStringed(time: Long): String{
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val cldr = Calendar.getInstance()
        cldr.timeInMillis = time
        return formatter.format(cldr.timeInMillis)
    }

    fun getDayStats(responseObject: ArrayList<PriceData>?, currency: String): ArrayList<DayStats> {
        val dayStatsList = ArrayList<DayStats>()
        val s = responseObject?.size
        for(i in 0 until s!!){
            val date = responseObject[i].time_period_start.split("T")[0]
            val high = responseObject[i].rate_high
            val daysHighest = "Highest: $high $currency"
            val low = responseObject[i].rate_low
            val daysLowest = "Lowest: $low $currency"
            val dayStats = DayStats(date,daysHighest,daysLowest)
            dayStatsList.add(dayStats)
        }
        return dayStatsList
    }
}