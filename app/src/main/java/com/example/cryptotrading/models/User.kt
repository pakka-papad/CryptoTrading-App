package com.example.cryptotrading.models

class User(
    val uid: String = "",
    val displayName: String? = "",
    val imageUrl: String = "",
    var currency: String = "",
    var favourites: ArrayList<String> =  ArrayList())
