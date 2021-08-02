package com.example.cryptotrading.models

class PriceData(val time_period_start: String = "",
    val time_period_end: String = "",
    val time_open: String = "",
    val time_close: String = "",
    val rate_open: Double = 0.0,
    val rate_high: Double = 0.0,
    val rate_low: Double = 0.0,
    val rate_close: Double = 0.0)